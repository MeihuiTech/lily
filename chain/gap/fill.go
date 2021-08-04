package gap

import (
	"context"

	"github.com/filecoin-project/sentinel-visor/chain"
	"github.com/filecoin-project/sentinel-visor/lens"
	"github.com/filecoin-project/sentinel-visor/model/visor"
	"github.com/filecoin-project/sentinel-visor/storage"
	"github.com/go-pg/pg/v10"
	logging "github.com/ipfs/go-log/v2"
	"golang.org/x/xerrors"
)

var log = logging.Logger("visor/gap")

type GapFiller struct {
	DB                   *storage.Database
	opener               lens.APIOpener
	name                 string
	minHeight, maxHeight uint64
	tasks                []string
}

func NewGapFiller(o lens.APIOpener, db *storage.Database, name string, maxHeight, minHeight uint64, tasks []string) *GapFiller {
	return &GapFiller{
		DB:        db,
		opener:    o,
		name:      name,
		maxHeight: maxHeight,
		minHeight: minHeight,
		tasks:     tasks,
	}
}

func (g *GapFiller) Run(ctx context.Context) error {
	gaps, err := g.queryGaps(ctx)
	if err != nil {
		return err
	}
	fillLog := log.With("type", "fill")
	fillLog.Infow("run", "count", len(gaps))

	for idx, gap := range gaps {
		// TODO we could optimize here by collecting all gaps at same height and launching a single instance of the walker for them
		indexer, err := chain.NewTipSetIndexer(g.opener, g.DB, 0, g.name, []string{gap.Task})
		if err != nil {
			gap.Status = err.Error()
			log.Errorw("fill failed", "height", gap.Height, "error", err.Error())
		} else {
			walker := chain.NewWalker(indexer, g.opener, gap.Height, gap.Height)
			if err := walker.Run(ctx); err != nil {
				gap.Status = err.Error()
				log.Errorw("fill failed", "height", gap.Height, "error", err.Error())
			} else {
				gap.Status = "FILLED"
				fillLog.Infow("fill success", "height", gap.Height, "remaining", len(gaps)-idx)
			}
		}

		// TODO we need to fill these as we process them to avoid failing to mark them complete in the event of a fauluer
		if err := g.setGapFilled(ctx, gap); err != nil {
			return err
		}
	}
	return nil
}

func (g *GapFiller) queryGaps(ctx context.Context) ([]*visor.GapReport, error) {
	var out []*visor.GapReport
	if len(g.tasks) != 0 {
		if err := g.DB.AsORM().ModelContext(ctx, &out).
			Order("height desc").
			Where("status = ?", "GAP").
			Where("task = ANY (?)", pg.Array(g.tasks)).
			Where("height >= ?", g.minHeight).
			Where("height <= ?", g.maxHeight).
			Select(); err != nil {
			return nil, xerrors.Errorf("querying gap reports: %w", err)
		}
	} else {
		if err := g.DB.AsORM().ModelContext(ctx, &out).
			Order("height desc").
			Where("status = ?", "GAP").
			Where("height >= ?", g.minHeight).
			Where("height <= ?", g.maxHeight).
			Select(); err != nil {
			return nil, xerrors.Errorf("querying gap reports: %w", err)
		}
	}
	return out, nil
}

func (g *GapFiller) setGapFilled(ctx context.Context, gap *visor.GapReport) error {
	_, err := g.DB.AsORM().ModelContext(ctx, gap).
		Set("status = 'FILLED'").
		Where("height = ?", gap.Height).
		Where("task = ?", gap.Task).
		Update()
	return err
}
