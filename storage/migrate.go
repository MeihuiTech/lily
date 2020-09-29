package storage

import (
	"context"
	"strconv"

	_ "github.com/filecoin-project/sentinel-visor/storage/migrations"
	"github.com/go-pg/migrations/v8"
	"github.com/go-pg/pg/v10"
	"golang.org/x/xerrors"
)

// GetSchemaVersions returns the schema version in the database and the latest schema version defined by the available
// migrations.
func (d *Database) GetSchemaVersions(ctx context.Context) (int, int, error) {
	// If we're already connected then use that connection
	if d.DB != nil {
		return getSchemaVersions(ctx, d.DB)
	}

	// Temporarily connect
	db, err := connect(ctx, d.opt)
	if err != nil {
		return 0, 0, xerrors.Errorf("connect: %w", err)
	}
	defer db.Close()
	return getSchemaVersions(ctx, db)
}

// getSchemaVersions returns the schema version in the database and the schema version defined by the available
// migrations.
func getSchemaVersions(ctx context.Context, db *pg.DB) (int, int, error) {
	// Run the migration init to ensure we always have a migrations table
	_, _, err := migrations.Run(db, "init")
	if err != nil {
		return 0, 0, xerrors.Errorf("migration table init: %w", err)
	}

	dbVersion, err := migrations.Version(db)
	if err != nil {
		return 0, 0, xerrors.Errorf("unable to determine schema version: %w", err)
	}

	// Current desired schema version is based on the highest migration version
	var desiredVersion int64
	ms := migrations.DefaultCollection.Migrations()
	for _, m := range ms {
		if m.Version > desiredVersion {
			desiredVersion = m.Version
		}
	}

	return int(dbVersion), int(desiredVersion), nil
}

// MigrateSchema migrates the database schema to the latest version based on the list of migrations available
func (d *Database) MigrateSchema(ctx context.Context) error {
	db, err := connect(ctx, d.opt)
	if err != nil {
		return xerrors.Errorf("connect: %w", err)
	}
	defer db.Close()

	dbVersion, latestVersion, err := getSchemaVersions(ctx, db)
	if err != nil {
		return xerrors.Errorf("get schema versions: %w", err)
	}
	log.Infof("current database schema is version %d", dbVersion)

	if dbVersion == latestVersion {
		log.Info("current database schema is at latest version, no migration needed")
		return nil
	}

	// Acquire an exclusive lock on the schema so we know no other instances are running
	if err := SchemaLock.LockExclusive(ctx, db); err != nil {
		return xerrors.Errorf("acquiring schema lock: %w", err)
	}

	// Remember to release the lock
	defer func() {
		err := SchemaLock.UnlockExclusive(ctx, db)
		if err != nil {
			log.Errorf("failed to release exclusive lock: %v", err)
		}
	}()

	log.Infof("running schema migration from version %d to version %d", dbVersion, latestVersion)
	_, newDBVersion, err := migrations.Run(db, "up")
	if err != nil {
		return xerrors.Errorf("run migration: %w", err)
	}
	log.Infof("current database schema is now version %d", newDBVersion)
	return nil
}

// MigrateSchema migrates the database schema to a specific version. Note that downgrading a schema to an earlier
// version is destructive and may result in the loss of data.
func (d *Database) MigrateSchemaTo(ctx context.Context, target int) error {
	db, err := connect(ctx, d.opt)
	if err != nil {
		return xerrors.Errorf("connect: %w", err)
	}
	defer db.Close()

	dbVersion, latestVersion, err := getSchemaVersions(ctx, db)
	if err != nil {
		return xerrors.Errorf("get schema versions: %w", err)
	}
	log.Infof("current database schema is version %d", dbVersion)

	if latestVersion < target {
		return xerrors.Errorf("no migrations found for version %d", target)
	}

	if dbVersion == target {
		return xerrors.Errorf("database schema is already at version %d", dbVersion)
	}

	// Acquire an exclusive lock on the schema so we know no other instances are running
	if err := SchemaLock.LockExclusive(ctx, db); err != nil {
		return xerrors.Errorf("acquiring schema lock: %w", err)
	}

	// Remember to release the lock
	defer func() {
		err := SchemaLock.UnlockExclusive(ctx, db)
		if err != nil {
			log.Errorf("failed to release exclusive lock: %v", err)
		}
	}()

	// Do we need to rollback schema version
	if dbVersion > target {
		for dbVersion > target {
			log.Warnf("running destructive schema migration from version %d to version %d", dbVersion, dbVersion-1)
			_, newDBVersion, err := migrations.Run(db, "down")
			if err != nil {
				return xerrors.Errorf("run migration: %w", err)
			}
			dbVersion = int(newDBVersion)
			log.Infof("current database schema is now version %d", dbVersion)
		}
		return nil
	}

	// Need to advance schema version
	log.Infof("running schema migration from version %d to version %d", dbVersion, target)
	_, newDBVersion, err := migrations.Run(db, "up", strconv.Itoa(target))
	if err != nil {
		return xerrors.Errorf("run migration: %w", err)
	}
	log.Infof("current database schema is now version %d", newDBVersion)

	return nil
}
