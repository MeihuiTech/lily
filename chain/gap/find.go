package gap

import (
	"context"
	"time"

	"github.com/deckarep/golang-set"
	"github.com/filecoin-project/go-state-types/abi"
	"github.com/filecoin-project/lotus/chain/types"
	"github.com/filecoin-project/sentinel-visor/chain"
	"github.com/filecoin-project/sentinel-visor/lens"
	"github.com/filecoin-project/sentinel-visor/model/visor"
	"github.com/filecoin-project/sentinel-visor/storage"
	"golang.org/x/xerrors"
)

type GapIndexer struct {
	DB                   *storage.Database
	opener               lens.APIOpener
	name                 string
	minHeight, maxHeight uint64
}

var TaskSet mapset.Set

func init() {
	TaskSet = mapset.NewSet()
	for _, t := range chain.AllTasks {
		TaskSet.Add(t)
	}
}

func NewGapIndexer(o lens.APIOpener, db *storage.Database, name string, maxHeight, minHeight uint64) *GapIndexer {
	return &GapIndexer{
		DB:        db,
		opener:    o,
		name:      name,
		maxHeight: maxHeight,
		minHeight: minHeight,
	}
}

func (g *GapIndexer) Run(ctx context.Context) error {
	startTime := time.Now()
	node, closer, err := g.opener.Open(ctx)
	if err != nil {
		return xerrors.Errorf("open lens: %w", err)
	}
	defer func() {
		closer()
	}()

	head, err := node.ChainHead(ctx)
	if err != nil {
		return err
	}
	maxHeight := g.maxHeight
	if uint64(head.Height()) < g.maxHeight {
		maxHeight = uint64(head.Height())
	}

	findLog := log.With("type", "find")

	taskGaps, err := g.findTaskEpochGaps(ctx, len(chain.AllTasks), maxHeight, g.minHeight)
	if err != nil {
		return xerrors.Errorf("finding task epoch gaps: %w", err)
	}
	findLog.Infow("found gaps in tasks", "count", len(taskGaps))

	heightGaps, nulls, err := g.findEpochGapsAndNullRounds(ctx, node, maxHeight, g.minHeight)
	if err != nil {
		return xerrors.Errorf("finding epoch gaps: %w", err)
	}
	findLog.Infow("found gaps in epochs", "count", len(heightGaps))
	findLog.Infow("found null rounds", "count", len(nulls))

	skipGaps, err := g.findEpochSkips(ctx, maxHeight, g.minHeight)
	if err != nil {
		return xerrors.Errorf("detecting skipped gaps: %w", err)
	}
	findLog.Infow("found skipped epochs", "count", len(skipGaps))

	var nullRounds visor.ProcessingReportList
	for _, epoch := range nulls {
		nullRounds = append(nullRounds, &visor.ProcessingReport{
			Height:      int64(epoch),
			StateRoot:   "NULL_ROUND",
			Reporter:    g.name,
			Task:        "NULL_ROUND",
			StartedAt:   startTime,
			CompletedAt: time.Now(),
			Status:      visor.ProcessingStatusOK,
		})
	}

	return g.DB.PersistBatch(ctx, skipGaps, heightGaps, taskGaps, nullRounds)
}

type GapIndexerLens interface {
	ChainGetTipSetByHeight(ctx context.Context, epoch abi.ChainEpoch, tsk types.TipSetKey) (*types.TipSet, error)
}

func (g *GapIndexer) findEpochSkips(ctx context.Context, max, min uint64) (visor.GapReportList, error) {
	log.Debug("finding skipped epochs")
	reportTime := time.Now()

	var skippedReports []visor.ProcessingReport
	if err := g.DB.AsORM().ModelContext(ctx, &skippedReports).
		Order("height desc").
		Where("status = ?", visor.ProcessingStatusSkip).
		Where("height >= ?", min).
		Where("height <= ?", max).
		Select(); err != nil {
		return nil, xerrors.Errorf("query processing report skips: %w", err)
	}
	log.Debugw("executed find skipped epoch query", "count", len(skippedReports))

	gapReport := make([]*visor.GapReport, len(skippedReports))
	for idx, r := range skippedReports {
		gapReport[idx] = &visor.GapReport{
			Height:     r.Height,
			Task:       r.Task,
			Status:     "GAP",
			Reporter:   g.name,
			ReportedAt: reportTime,
		}
	}
	return gapReport, nil
}

func (g *GapIndexer) findEpochGapsAndNullRounds(ctx context.Context, node GapIndexerLens, max, min uint64) (visor.GapReportList, []abi.ChainEpoch, error) {
	log.Debug("finding epoch gaps and null rounds")
	reportTime := time.Now()

	var nullRounds []abi.ChainEpoch
	var missingHeights []uint64
	res, err := g.DB.AsORM().QueryContext(
		ctx,
		&missingHeights,
		`
		SELECT s.i AS missing_epoch
		FROM generate_series(?, ?) s(i)
		WHERE NOT EXISTS (SELECT 1 FROM visor_processing_reports WHERE height = s.i);
		`,
		min, max)
	if err != nil {
		return nil, nil, err
	}
	log.Debugw("executed find epoch gap query", "count", res.RowsReturned())

	gapReport := make([]*visor.GapReport, 0, len(missingHeights))
	// walk the possible gaps and query lotus to determine if gap was a null round or missed epoch.
	for _, gap := range missingHeights {
		gh := abi.ChainEpoch(gap)
		tsgap, err := node.ChainGetTipSetByHeight(ctx, gh, types.EmptyTSK)
		if err != nil {
			return nil, nil, xerrors.Errorf("getting tipset by height %d: %w", gh, err)
		}
		if tsgap.Height() == gh {
			log.Debugw("found gap", "height", gh)
			for _, task := range chain.AllTasks {
				gapReport = append(gapReport, &visor.GapReport{
					Height:     int64(tsgap.Height()),
					Task:       task,
					Status:     "GAP",
					Reporter:   g.name, // TODO does this really need a name?
					ReportedAt: reportTime,
				})
			}
		} else {
			log.Debugw("found null round", "height", gh)
			nullRounds = append(nullRounds, gh)
		}
	}
	return gapReport, nullRounds, nil
}

type TaskHeight struct {
	Task   string
	Height uint64
}

func (g *GapIndexer) findTaskEpochGaps(ctx context.Context, expectedTasksPerHeight int, max, min uint64) (visor.GapReportList, error) {
	log.Debug("finding task epoch gaps")
	start := time.Now()
	var result []TaskHeight
	var out visor.GapReportList
	// returns a list of tasks and heights for all incomplete heights
	// and incomplete height is a height with less than 12 (len.chainAllTasks) entries
	res, err := g.DB.AsORM().QueryContext(
		ctx,
		&result,
		`SELECT vpr.task, vpr.height
				FROM visor_processing_reports vpr
				LEFT JOIN
					(
						SELECT hc.height, hc.cheight
						FROM (
								SELECT height, COUNT(height) AS cheight
								FROM visor_processing_reports
								GROUP BY height
							) hc
				WHERE hc.cheight != ?
					) incomplete
				ON vpr.height = incomplete.height
				where vpr.height >= ? and vpr.height <= ?`,
		expectedTasksPerHeight, min, max,
	)
	if err != nil {
		return nil, err
	}
	log.Debugw("executed find task epoch gap query", "count", res.RowsReturned())

	// result has all the tasks completed at each height, now we need to find what is missing
	// at each height.
	var taskMap = make(map[uint64][]string)
	for _, th := range result {
		taskMap[th.Height] = append(taskMap[th.Height], th.Task)
	}

	for height, tasks := range taskMap {
		querySet := mapset.NewSet()
		for _, t := range tasks {
			querySet.Add(t)
		}
		missingTasks := TaskSet.Difference(querySet)
		log.Debugw("found tasks with gaps", "height", height, "missing", missingTasks.String())
		for mt := range missingTasks.Iter() {
			missing := mt.(string)
			out = append(out, &visor.GapReport{
				Height:     int64(height),
				Task:       missing,
				Status:     "GAP",
				Reporter:   g.name,
				ReportedAt: start,
			})
		}
	}
	return out, nil
}
