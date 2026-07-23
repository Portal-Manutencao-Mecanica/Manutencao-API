create table organization (
    organization_id uuid primary key,
    name varchar(150) not null,
    type varchar(30) not null,
    email_domain varchar(150) not null,
    active boolean not null default true,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    version bigint not null default 0,
    constraint uk_organization_name unique (name),
    constraint uk_organization_email_domain unique (email_domain),
    constraint ck_organization_type check (type in ('SENAI', 'WEG', 'OTHER'))
);

insert into organization (
    organization_id,
    name,
    type,
    email_domain,
    active,
    created_at,
    updated_at,
    version
)
values
    ('00000000-0000-4000-8000-000000000001', 'SENAI', 'SENAI', 'sesisenai.org.br', true, current_timestamp, current_timestamp, 0),
    ('00000000-0000-4000-8000-000000000002', 'WEG', 'WEG', 'weg.net', true, current_timestamp, current_timestamp, 0),
    ('00000000-0000-4000-8000-000000000003', 'Desenvolvimento Local', 'OTHER', 'local.com', true, current_timestamp, current_timestamp, 0);

alter table users
    add column username varchar(50),
    add column organization_id uuid,
    add column password_change_required boolean not null default false,
    add column temporary_password_expires_at timestamp(6),
    add column password_changed_at timestamp(6),
    add column failed_login_attempts integer not null default 0,
    add column locked_until timestamp(6),
    add column last_failed_login_at timestamp(6),
    add column lockout_count integer not null default 0,
    add column version bigint not null default 0;

with username_candidates as (
    select
        user_id,
        left(
            coalesce(
                nullif(regexp_replace(lower(split_part(user_email, '@', 1)), '[^a-z0-9._-]', '', 'g'), ''),
                'user'
            ),
            40
        ) as base_username
    from users
),
numbered_usernames as (
    select
        user_id,
        base_username,
        row_number() over (partition by base_username order by user_id) as occurrence
    from username_candidates
)
update users target
   set username = case
       when source.occurrence = 1 then source.base_username
       else left(source.base_username, 44) || '-' || source.occurrence::text
   end
  from numbered_usernames source
 where target.user_id = source.user_id;

update users
   set organization_id = case
       when lower(split_part(user_email, '@', 2)) = 'sesisenai.org.br'
         or lower(split_part(user_email, '@', 2)) like '%.sesisenai.org.br'
           then '00000000-0000-4000-8000-000000000001'::uuid
       when lower(split_part(user_email, '@', 2)) = 'weg.net'
         or lower(split_part(user_email, '@', 2)) like '%.weg.net'
           then '00000000-0000-4000-8000-000000000002'::uuid
       else '00000000-0000-4000-8000-000000000003'::uuid
   end;

alter table users
    alter column username set not null,
    alter column organization_id set not null,
    add constraint uk_users_username unique (username),
    add constraint fk_users_organization
        foreign key (organization_id)
        references organization(organization_id),
    add constraint ck_users_failed_login_attempts
        check (failed_login_attempts >= 0),
    add constraint ck_users_lockout_count
        check (lockout_count >= 0);

create unique index uk_users_email_lower
    on users (lower(user_email));

create index idx_users_organization
    on users (organization_id);

create index idx_users_locked_until
    on users (locked_until)
    where locked_until is not null;

create table audit_log (
    audit_log_id uuid primary key,
    user_id uuid,
    username varchar(150),
    action varchar(80) not null,
    entity_type varchar(80),
    entity_id uuid,
    endpoint varchar(255),
    http_method varchar(10),
    ip_address varchar(64),
    user_agent varchar(500),
    created_at timestamp(6) not null,
    success boolean not null,
    details text
);

create index idx_audit_log_user on audit_log (user_id);
create index idx_audit_log_action on audit_log (action);
create index idx_audit_log_entity on audit_log (entity_type, entity_id);
create index idx_audit_log_created_at on audit_log (created_at desc);
create index idx_audit_log_ip on audit_log (ip_address);
