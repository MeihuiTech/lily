package gap

import (
	"context"
	"sort"
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
	DB     *storage.Database
	opener lens.APIOpener
}

var TaskSet mapset.Set

func init() {
	for _, t := range chain.AllTasks {
		TaskSet.Add(t)
	}
}

func NewGapIndexer(o lens.APIOpener, db *storage.Database) *GapIndexer {
	return &GapIndexer{
		DB:     db,
		opener: o,
	}
}

func (g *GapIndexer) Run(ctx context.Context) error {
	node, closer, err := g.opener.Open(ctx)
	if err != nil {
		return xerrors.Errorf("open lens: %w", err)
	}
	defer func() {
		closer()
	}()

	findLog := log.With("type", "find")
	missingTasks, err := g.queryTaskGaps(ctx, len(chain.AllTasks))
	if err != nil {
		return xerrors.Errorf("detecting missing tasks: %w", err)
	}
	heightGaps, err := g.detectProcessingGaps(ctx, node)
	if err != nil {
		return xerrors.Errorf("detecting processing gaps: %w", err)
	}

	findLog.Infow("detected gaps in height", "count", len(heightGaps))

	skipGaps, err := g.detectSkippedTipSets(ctx, node)
	if err != nil {
		return xerrors.Errorf("detecting skipped gaps: %w", err)
	}

	findLog.Infow("detected gaps from skip", "count", len(skipGaps))

	return g.DB.PersistBatch(ctx, skipGaps, heightGaps)
}

func (g *GapIndexer) detectSkippedTipSets(ctx context.Context, node lens.API) (visor.GapReportList, error) {
	reportTime := time.Now()
	// TODO unsure how big these lists will be, will likely want some sort of pagination here with limits and a loop over it
	var skippedReports []visor.ProcessingReport
	if err := g.DB.AsORM().Model(&skippedReports).Order("height desc").Where("status = ?", visor.ProcessingStatusSkip).Select(); err != nil {
		return nil, xerrors.Errorf("query processing report skips: %w", err)
	}
	gapReport := make([]*visor.GapReport, len(skippedReports))
	for idx, r := range skippedReports {
		tsgap, err := node.ChainGetTipSetByHeight(ctx, abi.ChainEpoch(r.Height), types.EmptyTSK)
		if err != nil {
			return nil, xerrors.Errorf("getting tipset by height %d: %w", r.Height, err)
		}
		gapReport[idx] = &visor.GapReport{
			Height:     r.Height,
			TipSet:     tsgap.Key().String(),
			Task:       r.Task,
			Status:     "GAP",
			Reporter:   "gapIndexer", // TODO does this really need a name?
			ReportedAt: reportTime,
		}
	}
	return gapReport, nil
}

func (g *GapIndexer) detectProcessingGaps(ctx context.Context, node lens.API, start, end uint64) (visor.GapReportList, error) {
	reportTime := time.Now()

	var missingHeights []uint64
	g.DB.AsORM().QueryContext(
		ctx,
		&missingHeights,
		`
SELECT s.i AS missing_epoch
FROM generate_series(?, ?) s(i)
WHERE NOT EXISTS (SELECT 1 FROM visor_processing_reports WHERE height = s.i);
`,
		start, end)

	gapReport := make([]*visor.GapReport, 0, len(missingHeights))
	// walk the possible gaps and query lotus to determine if gap was a null round or missed epoch.
	for _, gap := range missingHeights {
		gh := abi.ChainEpoch(gap)
		tsgap, err := node.ChainGetTipSetByHeight(ctx, gh, types.EmptyTSK)
		if err != nil {
			return nil, xerrors.Errorf("getting tipset by height %d: %w", gh, err)
		}
		if tsgap.Height() == gh {
			for _, task := range chain.AllTasks {
				gapReport = append(gapReport, &visor.GapReport{
					Height:     int64(tsgap.Height()),
					TipSet:     tsgap.Key().String(),
					Task:       task,
					Status:     "GAP",
					Reporter:   "gapIndexer", // TODO does this really need a name?
					ReportedAt: reportTime,
				})
			}
		}
	}
	return gapReport, nil
}

type TaskHeight struct {
	Task   string
	Height uint64
}

type TasksTODO struct {
	Tasks  []string
	Height uint64
}

// queryTaskGaps returns a list of TasksTODO containing heights and the tasks missing at that height.
func (g *GapIndexer) queryTaskGaps(ctx context.Context, expectedTasksPerHeight int) ([]TasksTODO, error) {
	var result []TaskHeight
	var out []TasksTODO
	// returns a list of tasks and heights for all incomplete heights
	// and incomplete height is a height with less than 12 (len.chainAllTasks) entries
	if _, err := g.DB.AsORM().QueryContext(
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
				ON vpr.height = incomplete.height`,
		expectedTasksPerHeight,
	); err != nil {
		return nil, err
	}

	// result has all the tasks completed at each height, now we need to find what is missing
	// at each height.
	var taskMap = make(map[uint64][]string)
	for _, th := range result {
		taskMap[th.Height] = append(taskMap[th.Height], th.Task)
	}

	for height, tasks := range taskMap {
		// unsafe means not thread safe, which is fine.
		querySet := mapset.NewThreadUnsafeSet()
		for _, t := range tasks {
			querySet.Add(t)
		}
		missingTasks := TaskSet.Difference(querySet)
		for mt := range missingTasks.Iter() {
			missing := mt.([]string)
			out = append(out, TasksTODO{
				Tasks:  missing,
				Height: height,
			})
		}
	}
	return out, nil
}
