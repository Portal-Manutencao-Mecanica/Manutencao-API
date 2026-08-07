DO $$
DECLARE
    foreign_key record;
    delete_action text;
BEGIN
    FOR foreign_key IN
        SELECT
            constraint_info.conname,
            source_table.relname AS source_table_name,
            referenced_table.relname AS referenced_table_name,
            source_attribute.attname AS source_column_name
        FROM pg_constraint constraint_info
        JOIN pg_class source_table
            ON source_table.oid = constraint_info.conrelid
        JOIN pg_class referenced_table
            ON referenced_table.oid = constraint_info.confrelid
        JOIN pg_namespace namespace_info
            ON namespace_info.oid = source_table.relnamespace
        JOIN unnest(constraint_info.conkey) AS key_info(attnum)
            ON true
        JOIN pg_attribute source_attribute
            ON source_attribute.attrelid = source_table.oid
            AND source_attribute.attnum = key_info.attnum
        WHERE constraint_info.contype = 'f'
          AND constraint_info.confrelid IN (
              'public.student'::regclass,
              'public.teacher'::regclass
          )
          AND namespace_info.nspname = 'public'
    LOOP
        delete_action := CASE
            WHEN foreign_key.conname IN (
                'fk_autonomous_maintenance_students_student',
                'fk_class_group_student_student',
                'fk_inconvenience_5s_student_student',
                'fk_machine_log_student_student',
                'fk_maintenance_request_student_student',
                'fk_class_group_teacher_teacher'
            ) THEN 'CASCADE'
            ELSE 'SET NULL'
        END;

        IF delete_action = 'SET NULL' THEN
            EXECUTE format(
                'ALTER TABLE public.%I ALTER COLUMN %I DROP NOT NULL',
                foreign_key.source_table_name,
                foreign_key.source_column_name
            );
        END IF;

        EXECUTE format(
            'ALTER TABLE public.%I DROP CONSTRAINT %I',
            foreign_key.source_table_name,
            foreign_key.conname
        );
        EXECUTE format(
            'ALTER TABLE public.%I ADD CONSTRAINT %I FOREIGN KEY (%I) REFERENCES public.%I(user_id) ON DELETE %s',
            foreign_key.source_table_name,
            foreign_key.conname,
            foreign_key.source_column_name,
            foreign_key.referenced_table_name,
            delete_action
        );
    END LOOP;
END $$;
