DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'library_entry'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'library_entry'
              AND column_name = 'library_entry_key'
        ) THEN
            ALTER TABLE library_entry ALTER COLUMN library_entry_key TYPE text;
        END IF;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'libraryentry'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'libraryentry'
              AND column_name = 'library_entry_key'
        ) THEN
            ALTER TABLE libraryentry ALTER COLUMN library_entry_key TYPE text;
        END IF;
    END IF;
END $$;