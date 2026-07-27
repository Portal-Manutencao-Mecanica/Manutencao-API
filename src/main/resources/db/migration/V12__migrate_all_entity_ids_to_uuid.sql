create extension if not exists pgcrypto;

create temporary table _uuid_columns (
    table_name varchar(63) not null,
    column_name varchar(63) not null,
    temporary_column_name varchar(63) not null,
    parent_table_name varchar(63),
    parent_column_name varchar(63),
    dependency_depth integer not null,
    was_not_null boolean not null,
    is_generated_identifier boolean not null,
    primary key (table_name, column_name)
) on commit drop;

insert into _uuid_columns (
    table_name,
    column_name,
    temporary_column_name,
    dependency_depth,
    was_not_null,
    is_generated_identifier
)
values
    ('users', 'user_id', 'user_id_uuid_tmp', 0, true, true),
    ('designation', 'designation_id', 'designation_id_uuid_tmp', 0, true, true),
    ('place', 'place_id', 'place_id_uuid_tmp', 0, true, true),
    ('class_group', 'class_group_id', 'class_group_id_uuid_tmp', 0, true, true),
    ('equipment', 'equipment_id', 'equipment_id_uuid_tmp', 0, true, true),
    ('machine', 'machine_id', 'machine_id_uuid_tmp', 0, true, true),
    ('machine_log', 'machine_log_id', 'machine_log_id_uuid_tmp', 0, true, true),
    ('autonomous_maintenance', 'autonomous_maintenance_id', 'autonomous_maintenance_id_uuid_tmp', 0, true, true),
    ('inconvenience_5s', 'inconvenience_5s_id', 'inconvenience_5s_id_uuid_tmp', 0, true, true),
    ('maintenance_request', 'maintenance_request_id', 'maintenance_request_id_uuid_tmp', 0, true, true),
    ('buy', 'buy_id', 'buy_id_uuid_tmp', 0, true, true),
    ('buy_item', 'buy_item_id', 'buy_item_id_uuid_tmp', 0, true, true),
    ('media', 'media_id', 'media_id_uuid_tmp', 0, true, true),
    ('history_log', 'history_log_id', 'history_log_id_uuid_tmp', 0, true, true),
    ('notification', 'notification_id', 'notification_id_uuid_tmp', 0, true, true),
    ('event', 'event_id', 'event_id_uuid_tmp', 0, true, true),
    ('helper_material', 'helper_material_id', 'helper_material_id_uuid_tmp', 0, true, true),
    ('history_log', 'entity_id', 'entity_id_uuid_tmp', 0, true, false);

do
$$
declare
    inserted_columns integer;
begin
    loop
        insert into _uuid_columns (
            table_name,
            column_name,
            temporary_column_name,
            parent_table_name,
            parent_column_name,
            dependency_depth,
            was_not_null,
            is_generated_identifier
        )
        select
            child_table.relname,
            child_column.attname,
            child_column.attname || '_uuid_tmp',
            parent_table.relname,
            parent_column.attname,
            parent_metadata.dependency_depth + 1,
            child_column.attnotnull,
            false
        from pg_constraint foreign_key
        join pg_class child_table
          on child_table.oid = foreign_key.conrelid
        join pg_namespace child_schema
          on child_schema.oid = child_table.relnamespace
        join pg_attribute child_column
          on child_column.attrelid = foreign_key.conrelid
         and child_column.attnum = foreign_key.conkey[1]
        join pg_class parent_table
          on parent_table.oid = foreign_key.confrelid
        join pg_namespace parent_schema
          on parent_schema.oid = parent_table.relnamespace
        join pg_attribute parent_column
          on parent_column.attrelid = foreign_key.confrelid
         and parent_column.attnum = foreign_key.confkey[1]
        join _uuid_columns parent_metadata
          on parent_metadata.table_name = parent_table.relname
         and parent_metadata.column_name = parent_column.attname
        where foreign_key.contype = 'f'
          and cardinality(foreign_key.conkey) = 1
          and cardinality(foreign_key.confkey) = 1
          and child_schema.nspname = 'public'
          and parent_schema.nspname = 'public'
        on conflict (table_name, column_name) do nothing;

        get diagnostics inserted_columns = row_count;
        exit when inserted_columns = 0;
    end loop;
end
$$;

do
$$
declare
    migrated_column record;
begin
    for migrated_column in
        select *
        from _uuid_columns
        order by dependency_depth, table_name, column_name
    loop
        execute format(
            'alter table public.%I add column %I uuid',
            migrated_column.table_name,
            migrated_column.temporary_column_name
        );
    end loop;

    for migrated_column in
        select *
        from _uuid_columns
        where parent_table_name is null
          and is_generated_identifier
        order by table_name, column_name
    loop
        execute format(
            'update public.%I set %I = gen_random_uuid() where %I is null',
            migrated_column.table_name,
            migrated_column.temporary_column_name,
            migrated_column.temporary_column_name
        );
    end loop;

    for migrated_column in
        select *
        from _uuid_columns
        where parent_table_name is not null
        order by dependency_depth, table_name, column_name
    loop
        execute format(
            'update public.%1$I child
                set %2$I = parent.%3$I
               from public.%4$I parent
              where child.%5$I = parent.%6$I',
            migrated_column.table_name,
            migrated_column.temporary_column_name,
            migrated_column.parent_column_name || '_uuid_tmp',
            migrated_column.parent_table_name,
            migrated_column.column_name,
            migrated_column.parent_column_name
        );
    end loop;

    update history_log history
       set entity_id_uuid_tmp = target.autonomous_maintenance_id_uuid_tmp
      from autonomous_maintenance target
     where history.entity_type = 'AUTONOMOUS_MAINTENANCE'
       and history.entity_id = target.autonomous_maintenance_id;

    update history_log history
       set entity_id_uuid_tmp = target.buy_id_uuid_tmp
      from buy target
     where history.entity_type = 'BUY'
       and history.entity_id = target.buy_id;

    update history_log history
       set entity_id_uuid_tmp = target.inconvenience_5s_id_uuid_tmp
      from inconvenience_5s target
     where history.entity_type = 'INCONVENIENCE_5S'
       and history.entity_id = target.inconvenience_5s_id;

    update history_log history
       set entity_id_uuid_tmp = target.maintenance_request_id_uuid_tmp
      from maintenance_request target
     where history.entity_type = 'MAINTENANCE_REQUEST'
       and history.entity_id = target.maintenance_request_id;

    update history_log history
       set entity_id_uuid_tmp = target.machine_log_id_uuid_tmp
      from machine_log target
     where history.entity_type = 'MACHINE_LOG'
       and history.entity_id = target.machine_log_id;

    update history_log history
       set entity_id_uuid_tmp = target.equipment_id_uuid_tmp
      from equipment target
     where history.entity_type = 'EQUIPMENT'
       and history.entity_id = target.equipment_id;

    update history_log history
       set entity_id_uuid_tmp = target.machine_id_uuid_tmp
      from machine target
     where history.entity_type = 'MACHINE'
       and history.entity_id = target.machine_id;

    update history_log history
       set entity_id_uuid_tmp = target.event_id_uuid_tmp
      from event target
     where history.entity_type = 'EVENT'
       and history.entity_id = target.event_id;

    update history_log
       set entity_id_uuid_tmp = gen_random_uuid()
     where entity_id_uuid_tmp is null;

    for migrated_column in
        select *
        from _uuid_columns
        where was_not_null
        order by dependency_depth, table_name, column_name
    loop
        execute format(
            'alter table public.%I alter column %I set not null',
            migrated_column.table_name,
            migrated_column.temporary_column_name
        );
    end loop;
end
$$;

create temporary table _uuid_constraints on commit drop as
select distinct
    table_schema.nspname as schema_name,
    table_definition.relname as table_name,
    constraint_definition.conname as constraint_name,
    constraint_definition.contype as constraint_type,
    pg_get_constraintdef(constraint_definition.oid) as definition
from pg_constraint constraint_definition
join pg_class table_definition
  on table_definition.oid = constraint_definition.conrelid
join pg_namespace table_schema
  on table_schema.oid = table_definition.relnamespace
where table_schema.nspname = 'public'
  and constraint_definition.contype in ('p', 'u', 'f', 'c', 'x')
  and (
      exists (
          select 1
          from unnest(constraint_definition.conkey) constrained_key(attnum)
          join pg_attribute constrained_column
            on constrained_column.attrelid = constraint_definition.conrelid
           and constrained_column.attnum = constrained_key.attnum
          join _uuid_columns migrated_column
            on migrated_column.table_name = table_definition.relname
           and migrated_column.column_name = constrained_column.attname
      )
      or exists (
          select 1
          from unnest(constraint_definition.confkey) referenced_key(attnum)
          join pg_class referenced_table
            on referenced_table.oid = constraint_definition.confrelid
          join pg_attribute referenced_column
            on referenced_column.attrelid = constraint_definition.confrelid
           and referenced_column.attnum = referenced_key.attnum
          join _uuid_columns migrated_column
            on migrated_column.table_name = referenced_table.relname
           and migrated_column.column_name = referenced_column.attname
      )
  );

do
$$
declare
    saved_constraint record;
    migrated_column record;
begin
    for saved_constraint in
        select *
        from _uuid_constraints
        order by
            case when constraint_type = 'f' then 0 else 1 end,
            table_name,
            constraint_name
    loop
        execute format(
            'alter table %I.%I drop constraint %I',
            saved_constraint.schema_name,
            saved_constraint.table_name,
            saved_constraint.constraint_name
        );
    end loop;

    for migrated_column in
        select *
        from _uuid_columns
        order by dependency_depth desc, table_name, column_name
    loop
        execute format(
            'alter table public.%I drop column %I',
            migrated_column.table_name,
            migrated_column.column_name
        );

        execute format(
            'alter table public.%I rename column %I to %I',
            migrated_column.table_name,
            migrated_column.temporary_column_name,
            migrated_column.column_name
        );
    end loop;

    for saved_constraint in
        select *
        from _uuid_constraints
        order by
            case
                when constraint_type in ('p', 'u') then 0
                when constraint_type in ('c', 'x') then 1
                when constraint_type = 'f' then 2
                else 3
            end,
            table_name,
            constraint_name
    loop
        execute format(
            'alter table %I.%I add constraint %I %s',
            saved_constraint.schema_name,
            saved_constraint.table_name,
            saved_constraint.constraint_name,
            saved_constraint.definition
        );
    end loop;
end
$$;

create index if not exists idx_users_email on users (lower(user_email));
create index if not exists idx_history_log_actor on history_log (actor_id);
create index if not exists idx_history_log_entity on history_log (entity_type, entity_id);
create index if not exists idx_event_student on event (student_id);
create index if not exists idx_event_teacher on event (teacher_id);
create index if not exists idx_event_equipment on event (equipment_id);
create index if not exists idx_event_machine on event (machine_id);
create index if not exists idx_event_place on event (place_id);
