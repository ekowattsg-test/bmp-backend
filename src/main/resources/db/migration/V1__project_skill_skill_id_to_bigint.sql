DO $$
DECLARE
    current_data_type TEXT;
BEGIN
    SELECT data_type
    INTO current_data_type
    FROM information_schema.columns
    WHERE table_schema = current_schema()
      AND table_name = 'project_skill'
      AND column_name = 'skill_id';

    IF current_data_type IS NULL THEN
        RAISE NOTICE 'Skipping migration: column project_skill.skill_id not found in schema %', current_schema();
    ELSIF current_data_type <> 'bigint' THEN
        EXECUTE $sql$
            ALTER TABLE project_skill
            ALTER COLUMN skill_id TYPE bigint
            USING (
                CASE
                    WHEN skill_id IS NULL THEN NULL
                    WHEN trim(skill_id::text) = '' THEN NULL
                    WHEN trim(skill_id::text) ~ '^-?\d+$' THEN trim(skill_id::text)::bigint
                    ELSE NULL
                END
            )
        $sql$;
    END IF;
END
$$;