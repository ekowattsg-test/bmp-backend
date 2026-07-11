DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'briefing_content'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'briefing_content'
              AND column_name = 'image_key'
        ) THEN
            ALTER TABLE briefing_content ALTER COLUMN image_key TYPE text;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'briefing_content'
              AND column_name = 'translated_text'
        ) THEN
            ALTER TABLE briefing_content ALTER COLUMN translated_text TYPE text;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'briefing_content'
              AND column_name = 'content_text'
        ) THEN
            ALTER TABLE briefing_content ALTER COLUMN content_text TYPE text;
        END IF;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'briefingcontent'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'briefingcontent'
              AND column_name = 'image_key'
        ) THEN
            ALTER TABLE briefingcontent ALTER COLUMN image_key TYPE text;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'briefingcontent'
              AND column_name = 'translated_text'
        ) THEN
            ALTER TABLE briefingcontent ALTER COLUMN translated_text TYPE text;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'briefingcontent'
              AND column_name = 'content_text'
        ) THEN
            ALTER TABLE briefingcontent ALTER COLUMN content_text TYPE text;
        END IF;
    END IF;
END $$;
