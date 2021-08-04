package commands

import (
	"fmt"
	"os"
	"strings"

	lotuscli "github.com/filecoin-project/lotus/cli"
	"github.com/filecoin-project/sentinel-visor/chain"
	"github.com/filecoin-project/sentinel-visor/lens/lily"
	"github.com/urfave/cli/v2"
)

type gapOps struct {
	apiAddr  string
	apiToken string
	storage  string
	tasks    string
	name     string
	min      uint64
	max      uint64
}

var gapFlags gapOps

var GapCmd = &cli.Command{
	Name:  "gap",
	Usage: "Launch gap filling and finding jobs",
	Subcommands: []*cli.Command{
		GapFillCmd,
		GapFindCmd,
	},
}

var GapFillCmd = &cli.Command{
	Name:  "fill",
	Usage: "Fill gaps in the database",
	Flags: []cli.Flag{
		&cli.StringFlag{
			Name:        "api",
			Usage:       "Address of visor api in multiaddr format.",
			EnvVars:     []string{"VISOR_API"},
			Value:       "/ip4/127.0.0.1/tcp/1234",
			Destination: &gapFlags.apiAddr,
		},
		&cli.StringFlag{
			Name:        "api-token",
			Usage:       "Authentication token for visor api.",
			EnvVars:     []string{"VISOR_API_TOKEN"},
			Value:       "",
			Destination: &gapFlags.apiToken,
		},
		&cli.StringFlag{
			Name:        "storage",
			Usage:       "Name of storage that results will be written to.",
			Value:       "",
			Destination: &gapFlags.storage,
		},
		&cli.StringFlag{
			Name:        "tasks",
			Usage:       "Comma separated list of tasks to fill. Each task is reported separately in the database. If empty all task will be filled.",
			Value:       "",
			Destination: &gapFlags.tasks,
		},
		&cli.StringFlag{
			Name:        "name",
			Usage:       "name of instance performing find",
			Value:       "gap_find",
			Destination: &gapFlags.name,
		},
		&cli.Uint64Flag{
			Name:        "max",
			Usage:       "max epoch to search for gaps in",
			Value:       1_000_000, // TODO some estimate of max chain height given the time,
			Destination: &gapFlags.max,
		},
		&cli.Uint64Flag{
			Name:        "min",
			Usage:       "min epoch to search for gaps in",
			Value:       0,
			Destination: &gapFlags.min,
		},
	},
	Action: func(cctx *cli.Context) error {
		ctx := lotuscli.ReqContext(cctx)
		fmt.Println(chain.AllTasks)

		api, closer, err := GetAPI(ctx, gapFlags.apiAddr, gapFlags.apiToken)
		if err != nil {
			return err
		}
		defer closer()

		var tasks []string
		if gapFlags.tasks == "" {
			tasks = chain.AllTasks
		} else {
			tasks = strings.Split(gapFlags.tasks, ",")
		}
		gapFindID, err := api.LilyGapFill(ctx, &lily.LilyGapFillConfig{
			RestartOnFailure:    false,
			RestartOnCompletion: false,
			RestartDelay:        0,
			Storage:             gapFlags.storage,
			Name:                gapFlags.name,
			Tasks:               tasks,
			MaxHeight:           gapFlags.max,
			MinHeight:           gapFlags.min,
		})
		if err != nil {
			return err
		}
		if _, err := fmt.Fprintf(os.Stdout, "Created Gap Job: %d", gapFindID); err != nil {
			return err
		}
		return nil
	},
}

var GapFindCmd = &cli.Command{
	Name:  "find",
	Usage: "find gaps in the database",
	Flags: []cli.Flag{
		&cli.StringFlag{
			Name:        "api",
			Usage:       "Address of visor api in multiaddr format.",
			EnvVars:     []string{"VISOR_API"},
			Value:       "/ip4/127.0.0.1/tcp/1234",
			Destination: &gapFlags.apiAddr,
		},
		&cli.StringFlag{
			Name:        "api-token",
			Usage:       "Authentication token for visor api.",
			EnvVars:     []string{"VISOR_API_TOKEN"},
			Value:       "",
			Destination: &gapFlags.apiToken,
		},
		&cli.StringFlag{
			Name:        "storage",
			Usage:       "Name of storage that results will be written to.",
			Value:       "",
			Destination: &gapFlags.storage,
		},
		&cli.StringFlag{
			Name:        "name",
			Usage:       "name of instance performing find",
			Value:       "gap_find",
			Destination: &gapFlags.name,
		},
		&cli.Uint64Flag{
			Name:        "max",
			Usage:       "max epoch to search for gaps in",
			Value:       1_000_000, // TODO some estimate of max chain height given the time,
			Destination: &gapFlags.max,
		},
		&cli.Uint64Flag{
			Name:        "min",
			Usage:       "min epoch to search for gaps in",
			Value:       0,
			Destination: &gapFlags.min,
		},
	},
	Action: func(cctx *cli.Context) error {
		ctx := lotuscli.ReqContext(cctx)

		api, closer, err := GetAPI(ctx, gapFlags.apiAddr, gapFlags.apiToken)
		if err != nil {
			return err
		}
		defer closer()

		gapFindID, err := api.LilyGapFind(ctx, &lily.LilyGapFindConfig{
			RestartOnFailure:    false,
			RestartOnCompletion: false,
			RestartDelay:        0,
			Storage:             gapFlags.storage,
			Name:                gapFlags.name,
			MaxHeight:           gapFlags.max,
			MinHeight:           gapFlags.min,
		})
		if err != nil {
			return err
		}
		if _, err := fmt.Fprintf(os.Stdout, "Created Gap Job: %d", gapFindID); err != nil {
			return err
		}
		return nil
	},
}
