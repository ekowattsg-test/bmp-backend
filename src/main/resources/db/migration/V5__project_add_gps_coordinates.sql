DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'project'
    ) THEN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'project'
              AND column_name = 'latitude'
        ) THEN
            ALTER TABLE project ADD COLUMN latitude varchar(64);
        END IF;

        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'project'
              AND column_name = 'longitude'
        ) THEN
            ALTER TABLE project ADD COLUMN longitude varchar(64);
        END IF;
    END IF;
END $$;
