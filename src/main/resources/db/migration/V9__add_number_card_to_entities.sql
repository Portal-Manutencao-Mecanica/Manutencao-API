-- Add a stable public card identifier to every persisted domain entity.
-- Existing rows receive deterministic values before the constraints are applied.
DO $$
DECLARE
    table_name text;
    tables text[] := ARRAY['users','autonomous_maintenance','buy','buy_item','class_group','designation','equipment','event','helper_material','history_log','inconvenience_5s','machine','machine_log','maintenance_request','media','notification','place'];
BEGIN
    FOREACH table_name IN ARRAY tables LOOP
        EXECUTE format('ALTER TABLE %I ADD COLUMN IF NOT EXISTS number_card varchar(255)', table_name);
        IF table_name = 'users' THEN
            EXECUTE 'UPDATE users SET number_card = ''LEGACY-'' || user_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'autonomous_maintenance' THEN
            EXECUTE 'UPDATE autonomous_maintenance SET number_card = ''LEGACY-'' || autonomous_maintenance_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'buy' THEN
            EXECUTE 'UPDATE buy SET number_card = ''LEGACY-'' || buy_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'buy_item' THEN
            EXECUTE 'UPDATE buy_item SET number_card = ''LEGACY-'' || buy_item_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'class_group' THEN
            EXECUTE 'UPDATE class_group SET number_card = ''LEGACY-'' || class_group_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'designation' THEN
            EXECUTE 'UPDATE designation SET number_card = ''LEGACY-'' || designation_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'equipment' THEN
            EXECUTE 'UPDATE equipment SET number_card = ''LEGACY-'' || equipment_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'event' THEN
            EXECUTE 'UPDATE event SET number_card = ''LEGACY-'' || event_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'helper_material' THEN
            EXECUTE 'UPDATE helper_material SET number_card = ''LEGACY-'' || helper_material_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'history_log' THEN
            EXECUTE 'UPDATE history_log SET number_card = ''LEGACY-'' || history_log_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'inconvenience_5s' THEN
            EXECUTE 'UPDATE inconvenience_5s SET number_card = ''LEGACY-'' || inconvenience_5s_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'machine' THEN
            EXECUTE 'UPDATE machine SET number_card = ''LEGACY-'' || machine_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'machine_log' THEN
            EXECUTE 'UPDATE machine_log SET number_card = ''LEGACY-'' || machine_log_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'maintenance_request' THEN
            EXECUTE 'UPDATE maintenance_request SET number_card = ''LEGACY-'' || maintenance_request_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'media' THEN
            EXECUTE 'UPDATE media SET number_card = ''LEGACY-'' || media_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'notification' THEN
            EXECUTE 'UPDATE notification SET number_card = ''LEGACY-'' || notification_id::text WHERE number_card IS NULL';
        ELSIF table_name = 'place' THEN
            EXECUTE 'UPDATE place SET number_card = ''LEGACY-'' || place_id::text WHERE number_card IS NULL';
        END IF;
        EXECUTE format('ALTER TABLE %I ALTER COLUMN number_card SET NOT NULL', table_name);
        EXECUTE format('CREATE UNIQUE INDEX IF NOT EXISTS %I ON %I (number_card)', 'uk_' || table_name || '_number_card', table_name);
    END LOOP;
END $$;