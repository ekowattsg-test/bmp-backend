DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'library_catelog'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'library_catelog'
              AND column_name = 'quic_search_key'
        ) THEN
            ALTER TABLE library_catelog ALTER COLUMN quic_search_key TYPE text;
        END IF;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'librarycatelog'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'librarycatelog'
              AND column_name = 'quic_search_key'
        ) THEN
            ALTER TABLE librarycatelog ALTER COLUMN quic_search_key TYPE text;
        END IF;
    END IF;

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
              AND column_name = 'entry_quick_search_key'
        ) THEN
            ALTER TABLE library_entry ALTER COLUMN entry_quick_search_key TYPE text;
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
              AND column_name = 'entry_quick_search_key'
        ) THEN
            ALTER TABLE libraryentry ALTER COLUMN entry_quick_search_key TYPE text;
        END IF;
    END IF;
END $$;