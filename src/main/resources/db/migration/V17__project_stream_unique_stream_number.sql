-- Renumber duplicate stream_number values within each project before adding the unique constraint.
-- Existing duplicates get assigned the next available numbers starting after the project's current max.
DO $$
DECLARE
    rec RECORD;
    next_num BIGINT;
BEGIN
    FOR rec IN
        SELECT project_stream_id, project_code,
               ROW_NUMBER() OVER (PARTITION BY project_code ORDER BY project_stream_id) AS rn
        FROM project_stream
        WHERE project_code IS NOT NULL
          AND stream_number IS NOT NULL
          AND (project_code, stream_number) IN (
              SELECT project_code, stream_number
              FROM project_stream
              WHERE project_code IS NOT NULL AND stream_number IS NOT NULL
              GROUP BY project_code, stream_number
              HAVING COUNT(*) > 1
          )
        ORDER BY project_code, project_stream_id
    LOOP
        IF rec.rn > 1 THEN
            SELECT COALESCE(MAX(stream_number), 0) + 1
            INTO next_num
            FROM project_stream
            WHERE project_code = rec.project_code;

            UPDATE project_stream
            SET stream_number = next_num
            WHERE project_stream_id = rec.project_stream_id;
        END IF;
    END LOOP;
END $$;

ALTER TABLE project_stream
    ADD COLUMN IF NOT EXISTS parent_stream_number BIGINT,
    ADD CONSTRAINT uk_project_stream_project_code_stream_number UNIQUE (project_code, stream_number);
