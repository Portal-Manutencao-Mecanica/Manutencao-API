DO $$
DECLARE
    foreign_key record;
    delete_action text;
BEGIN
    FOR foreign_key IN
        SELECT
            constraint_info.conname,
            table_info.relname AS table_name,
            attribute_info.attname AS column_name
        FROM pg_constraint constraint_info
        JOIN pg_class table_info ON table_info.oid = constraint_info.conrelid
        JOIN pg_namespace namespace_info ON namespace_info.oid = table_info.relnamespace
        JOIN unnest(constraint_info.conkey) AS key_info(attnum) ON true
        JOIN pg_attribute attribute_info
            ON attribute_info.attrelid = table_info.oid
            AND attribute_info.attnum = key_info.attnum
        WHERE constraint_info.contype = 'f'
          AND constraint_info.confrelid = 'public.users'::regclass
          AND namespace_info.nspname = 'public'
    LOOP
        delete_action := CASE
            WHEN foreign_key.conname IN (
                'fk_admin_user',
                'fk_coordinator_user',
                'fk_student_user',
                'fk_teacher_user',
                'fk_notification_preference_user',
                'fk_password_reset_token_user',
                'fk_refresh_token_user',
                'fk_first_access_code_user'
            ) THEN 'CASCADE'
            ELSE 'SET NULL'
        END;

        IF delete_action = 'SET NULL' THEN
            EXECUTE format(
                'ALTER TABLE public.%I ALTER COLUMN %I DROP NOT NULL',
                foreign_key.table_name,
                foreign_key.column_name
            );
        END IF;

        EXECUTE format(
            'ALTER TABLE public.%I DROP CONSTRAINT %I',
            foreign_key.table_name,
            foreign_key.conname
        );
        EXECUTE format(
            'ALTER TABLE public.%I ADD CONSTRAINT %I FOREIGN KEY (%I) REFERENCES public.users(user_id) ON DELETE %s',
            foreign_key.table_name,
            foreign_key.conname,
            foreign_key.column_name,
            delete_action
        );
    END LOOP;
END $$;