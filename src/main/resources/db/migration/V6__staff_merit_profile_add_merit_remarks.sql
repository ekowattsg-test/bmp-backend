DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'staff_merit_profile'
    ) THEN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'staff_merit_profile'
              AND column_name = 'merit_remarks'
        ) THEN
            ALTER TABLE staff_merit_profile ADD COLUMN merit_remarks varchar(255);
        END IF;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'staffmeritprofile'
    ) THEN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'staffmeritprofile'
              AND column_name = 'merit_remarks'
        ) THEN
            ALTER TABLE staffmeritprofile ADD COLUMN merit_remarks varchar(255);
        END IF;
    END IF;
END $$;
