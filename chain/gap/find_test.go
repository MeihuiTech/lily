package gap

import (
	"context"
	"sort"
	"testing"
	"time"

	"github.com/filecoin-project/go-address"
	"github.com/filecoin-project/go-state-types/abi"
	"github.com/filecoin-project/go-state-types/crypto"
	"github.com/filecoin-project/lotus/chain/types"
	"github.com/filecoin-project/sentinel-visor/chain"
	"github.com/filecoin-project/sentinel-visor/lens"
	"github.com/filecoin-project/sentinel-visor/model/visor"
	"github.com/filecoin-project/sentinel-visor/storage"
	"github.com/filecoin-project/sentinel-visor/testutil"
	"github.com/go-pg/pg/v10"
	"github.com/ipfs/go-cid"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/require"
)

var (
	minHeight = uint64(0)
	maxHeight = uint64(10)
)

func TestFind(t *testing.T) {
	/*
		if testing.Short() {
			t.Skip("short testing requested")
		}

	*/

	// TODO adjust timeout
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*30)
	defer cancel()

	db, cleanup, err := testutil.WaitForExclusiveDatabase(ctx, t)
	require.NoError(t, err)
	defer func() { require.NoError(t, cleanup()) }()

	t.Run("gap all tasks at epoch 1", func(t *testing.T) {
		truncateVPR(t, db)
		initializeVPR(t, db, maxHeight)
		gapEpochVPR(t, db, 1, chain.AllTasks...)

		strg, err := storage.NewDatabaseFromDB(ctx, db, "public")
		require.NoError(t, err, "NewDatabaseFromDB")

		tsh1 := fakeTipset(t, 1)
		mlens := new(MockedFindLens)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh1.Height(), types.EmptyTSK).
			Return(tsh1, nil)

		actual, nullRounds, err := NewGapIndexer(&FakeFindLensOpener{}, strg, t.Name(), maxHeight, minHeight).
			findEpochGapsAndNullRounds(ctx, mlens, maxHeight, minHeight)
		require.NoError(t, err)
		require.Len(t, nullRounds, 0)

		expected := makeGapReportList(tsh1, chain.AllTasks...)
		assertGapReportsEqual(t, expected, actual)
	})

	t.Run("gap all tasks at epoch 1 null rounds at epochs 5 6 7 9", func(t *testing.T) {
		truncateVPR(t, db)
		initializeVPR(t, db, maxHeight)
		gapEpochVPR(t, db, 1, chain.AllTasks...)
		gapEpochVPR(t, db, 5, chain.AllTasks...)
		gapEpochVPR(t, db, 6, chain.AllTasks...)
		gapEpochVPR(t, db, 7, chain.AllTasks...)
		gapEpochVPR(t, db, 9, chain.AllTasks...)

		strg, err := storage.NewDatabaseFromDB(ctx, db, "public")
		require.NoError(t, err, "NewDatabaseFromDB")

		tsh1 := fakeTipset(t, 1)
		mlens := new(MockedFindLens)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh1.Height(), types.EmptyTSK).
			Return(tsh1, nil)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, abi.ChainEpoch(5), types.EmptyTSK).
			Return(tsh1, nil)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, abi.ChainEpoch(6), types.EmptyTSK).
			Return(tsh1, nil)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, abi.ChainEpoch(7), types.EmptyTSK).
			Return(tsh1, nil)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, abi.ChainEpoch(9), types.EmptyTSK).
			Return(tsh1, nil)

		actual, nullRounds, err := NewGapIndexer(&FakeFindLensOpener{}, strg, t.Name(), maxHeight, minHeight).
			findEpochGapsAndNullRounds(ctx, mlens, maxHeight, minHeight)
		require.NoError(t, err)

		expected := makeGapReportList(tsh1, chain.AllTasks...)
		assertGapReportsEqual(t, expected, actual)

		assert.Len(t, nullRounds, 4)
		assert.Equal(t, nullRounds, []abi.ChainEpoch{5, 6, 7, 9})
	})

	t.Run("gap all tasks at epoch 1 4 5", func(t *testing.T) {
		truncateVPR(t, db)
		initializeVPR(t, db, maxHeight)
		gapEpochVPR(t, db, 1, chain.AllTasks...)
		gapEpochVPR(t, db, 4, chain.AllTasks...)
		gapEpochVPR(t, db, 5, chain.AllTasks...)

		strg, err := storage.NewDatabaseFromDB(ctx, db, "public")
		require.NoError(t, err, "NewDatabaseFromDB")

		tsh1 := fakeTipset(t, 1)
		tsh4 := fakeTipset(t, 4)
		tsh5 := fakeTipset(t, 5)
		mlens := new(MockedFindLens)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh1.Height(), types.EmptyTSK).
			Return(tsh1, nil)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh4.Height(), types.EmptyTSK).
			Return(tsh4, nil)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh5.Height(), types.EmptyTSK).
			Return(tsh5, nil)

		actual, nullRounds, err := NewGapIndexer(&FakeFindLensOpener{}, strg, t.Name(), maxHeight, minHeight).
			findEpochGapsAndNullRounds(ctx, mlens, maxHeight, minHeight)
		require.NoError(t, err)
		require.Len(t, nullRounds, 0)

		expected1 := makeGapReportList(tsh1, chain.AllTasks...)
		expected4 := makeGapReportList(tsh4, chain.AllTasks...)
		expected5 := makeGapReportList(tsh5, chain.AllTasks...)
		expected := append(expected1, expected4...)
		expected = append(expected, expected5...)
		assertGapReportsEqual(t, expected, actual)
	})

	t.Run("gap at epoch 2 for miner and init task", func(t *testing.T) {
		truncateVPR(t, db)
		initializeVPR(t, db, maxHeight)
		gapEpochVPR(t, db, 2, chain.ActorStatesMinerTask, chain.ActorStatesInitTask)

		strg, err := storage.NewDatabaseFromDB(ctx, db, "public")
		require.NoError(t, err, "NewDatabaseFromDB")

		tsh2 := fakeTipset(t, 2)
		mlens := new(MockedFindLens)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh2.Height(), types.EmptyTSK).
			Return(tsh2, nil)

		actual, err := NewGapIndexer(&FakeFindLensOpener{}, strg, t.Name(), maxHeight, minHeight).
			findTaskEpochGaps(ctx, len(chain.AllTasks), maxHeight, minHeight)
		require.NoError(t, err)

		expected := makeGapReportList(tsh2, chain.ActorStatesMinerTask, chain.ActorStatesInitTask)
		assertGapReportsEqual(t, expected, actual)
	})

	t.Run("gap at epoch 2 for miner and init task epoch 10 blocks messages market", func(t *testing.T) {
		truncateVPR(t, db)
		initializeVPR(t, db, maxHeight)
		gapEpochVPR(t, db, 2, chain.ActorStatesMinerTask, chain.ActorStatesInitTask)
		gapEpochVPR(t, db, 10, chain.BlocksTask, chain.MessagesTask, chain.ActorStatesMarketTask)

		strg, err := storage.NewDatabaseFromDB(ctx, db, "public")
		require.NoError(t, err, "NewDatabaseFromDB")

		tsh2 := fakeTipset(t, 2)
		tsh10 := fakeTipset(t, 10)
		mlens := new(MockedFindLens)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh2.Height(), types.EmptyTSK).
			Return(tsh2, nil)
		mlens.On("ChainGetTipSetByHeight", mock.Anything, tsh10.Height(), types.EmptyTSK).
			Return(tsh10, nil)

		actual, err := NewGapIndexer(&FakeFindLensOpener{}, strg, t.Name(), maxHeight, minHeight).
			findTaskEpochGaps(ctx, len(chain.AllTasks), maxHeight, minHeight)
		require.NoError(t, err)

		expected := makeGapReportList(tsh2, chain.ActorStatesMinerTask, chain.ActorStatesInitTask)
		expected = append(expected, makeGapReportList(tsh10, chain.BlocksTask, chain.MessagesTask, chain.ActorStatesMarketTask)...)
		assertGapReportsEqual(t, expected, actual)
	})

	t.Run("skip all tasks at epoch 1 and miner task at epoch 5", func(t *testing.T) {
		truncateVPR(t, db)
		initializeVPR(t, db, maxHeight)
		skipEpochSkippedVRP(t, db, 1, chain.AllTasks...)
		skipEpochSkippedVRP(t, db, 5, chain.ActorStatesMinerTask)

		strg, err := storage.NewDatabaseFromDB(ctx, db, "public")
		require.NoError(t, err, "NewDatabaseFromDB")

		actual, err := NewGapIndexer(&FakeFindLensOpener{}, strg, t.Name(), maxHeight, minHeight).
			findEpochSkips(ctx, maxHeight, minHeight)
		require.NoError(t, err)

		tsh1 := fakeTipset(t, 1)
		tsh5 := fakeTipset(t, 5)
		expected := makeGapReportList(tsh1, chain.AllTasks...)
		expected = append(expected, makeGapReportList(tsh5, chain.ActorStatesMinerTask)...)
		assertGapReportsEqual(t, expected, actual)
	})

}

type assertFields struct {
	status string
	task   string
}

func assertGapReportsEqual(t testing.TB, expected, actual visor.GapReportList) {
	assert.Equal(t, len(expected), len(actual))
	exp := make(map[int64][]assertFields, len(expected))
	act := make(map[int64][]assertFields, len(expected))

	for _, e := range expected {
		exp[e.Height] = append(exp[e.Height], assertFields{
			status: e.Status,
			task:   e.Task,
		})
	}

	for _, a := range actual {
		act[a.Height] = append(act[a.Height], assertFields{
			status: a.Status,
			task:   a.Task,
		})
	}

	for epoch := range exp {
		e := exp[epoch]
		a := act[epoch]
		sort.Slice(e, func(i, j int) bool {
			return e[i].task < e[j].task
		})
		sort.Slice(a, func(i, j int) bool {
			return a[i].task < a[j].task
		})
		assert.Equal(t, e, a)
	}
}

func makeGapReportList(ts *types.TipSet, tasks ...string) visor.GapReportList {
	var out visor.GapReportList
	for _, task := range tasks {
		out = append(out, &visor.GapReport{
			Height:     int64(ts.Height()),
			Task:       task,
			Status:     "GAP",
			Reporter:   "gapIndexer",
			ReportedAt: time.Date(2021, time.January, 1, 0, 0, 0, 0, time.UTC),
		})
	}
	return out
}

func gapEpochVPR(tb testing.TB, db *pg.DB, epoch int, tasks ...string) {
	for _, task := range tasks {
		_, err := db.Exec(
			`
	delete from visor_processing_reports
	where height = ? and task = ?
`,
			epoch, task)
		require.NoError(tb, err)
	}
}

func skipEpochSkippedVRP(tb testing.TB, db *pg.DB, epoch int, tasks ...string) {
	for _, task := range tasks {
		_, err := db.Exec(
			`
	update visor_processing_reports
	set status = 'SKIP'
	where height = ? and task = ?
`,
			epoch, task)
		require.NoError(tb, err)
	}
}

func truncateVPR(tb testing.TB, db *pg.DB) {
	_, err := db.Exec(`TRUNCATE TABLE visor_processing_reports`)
	require.NoError(tb, err, "visor_processing_report")
}

// fill the table at every epoch with every task for `count` epochs.
func initializeVPR(tb testing.TB, db *pg.DB, count uint64) {
	_, err := db.Exec(
		`
do $$
    DECLARE
        -- TODO add internal messages
        task_name text;
        arr text[] := array['actorstatesraw','actorstatespower','actorstatesreward','actorstatesminer','actorstatesinit','actorstatesmarket','actorstatesmultisig','blocks','messages','chaineconomics','msapprovals'];
    begin
        for epoch in 0..? loop
                foreach task_name in array arr loop
                insert into public.visor_processing_reports(height, state_root, reporter, task, started_at, completed_at, status, status_information, errors_detected)
                values(epoch, concat(epoch, '_state_root'), 'reporter', task_name, '2021-01-01 00:00:00.000000 +00:00', '2021-01-21 00:00:00.000000 +00:00', 'OK',null, null);
                    end loop;
            end loop;
    end;
$$;
`,
		count)
	require.NoError(tb, err)
}

type FakeFindLensOpener struct {
}

func (m *FakeFindLensOpener) Open(ctx context.Context) (lens.API, lens.APICloser, error) {
	return nil, func() {}, nil
}

type MockedFindLens struct {
	mock.Mock
}

func (m *MockedFindLens) ChainGetTipSetByHeight(ctx context.Context, epoch abi.ChainEpoch, tsk types.TipSetKey) (*types.TipSet, error) {
	args := m.Called(ctx, epoch, tsk)
	return args.Get(0).(*types.TipSet), args.Error(1)
}

func fakeTipset(t testing.TB, height int) *types.TipSet {
	bh := &types.BlockHeader{
		Miner:                 address.TestAddress,
		Height:                abi.ChainEpoch(height),
		ParentStateRoot:       testutil.RandomCid(),
		Parents:               []cid.Cid{testutil.RandomCid()},
		Messages:              testutil.RandomCid(),
		ParentMessageReceipts: testutil.RandomCid(),
		BlockSig:              &crypto.Signature{Type: crypto.SigTypeBLS},
		BLSAggregate:          &crypto.Signature{Type: crypto.SigTypeBLS},
		Timestamp:             uint64(time.Now().Unix()),
	}
	ts, err := types.NewTipSet([]*types.BlockHeader{bh})
	require.NoError(t, err)
	return ts
}
