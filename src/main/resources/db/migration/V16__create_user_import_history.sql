alter table users
    alter column user_name type varchar(150);

create table user_import (
    user_import_id uuid primary key,
    filename varchar(255) not null,
    imported_by uuid not null,
    organization_id uuid,
    status varchar(40) not null,
    total_rows integer not null default 0,
    created_count integer not null default 0,
    failed_count integer not null default 0,
    created_at timestamp(6) not null,
    completed_at timestamp(6),
    constraint fk_user_import_actor
        foreign key (imported_by) references users(user_id),
    constraint fk_user_import_organization
        foreign key (organization_id) references organization(organization_id),
    constraint ck_user_import_status
        check (status in ('PROCESSING', 'COMPLETED', 'COMPLETED_WITH_ERRORS', 'FAILED')),
    constraint ck_user_import_counts
        check (
            total_rows >= 0
            and created_count >= 0
            and failed_count >= 0
            and created_count + failed_count <= total_rows
        )
);

create table user_import_item (
    user_import_item_id uuid primary key,
    user_import_id uuid not null,
    row_number integer not null,
    name varchar(150),
    username varchar(50),
    email varchar(150),
    role varchar(30),
    organization_value varchar(150),
    status varchar(20) not null,
    created_user_id uuid,
    error_code varchar(80),
    error_field varchar(80),
    error_message varchar(500),
    created_at timestamp(6) not null,
    constraint fk_user_import_item_import
        foreign key (user_import_id) references user_import(user_import_id),
    constraint fk_user_import_item_created_user
        foreign key (created_user_id) references users(user_id),
    constraint uk_user_import_item_row unique (user_import_id, row_number),
    constraint ck_user_import_item_role
        check (role is null or role in ('ADMIN', 'ALUNO', 'PROFESSOR', 'COORDENADOR')),
    constraint ck_user_import_item_status
        check (status in ('CREATED', 'FAILED')),
    constraint ck_user_import_item_result
        check (
            (status = 'CREATED' and created_user_id is not null and error_code is null)
            or
            (status = 'FAILED' and created_user_id is null and error_code is not null)
        )
);

create index idx_user_import_actor on user_import(imported_by);
create index idx_user_import_organization on user_import(organization_id);
create index idx_user_import_created_at on user_import(created_at desc);
create index idx_user_import_item_import on user_import_item(user_import_id, row_number);
create index idx_user_import_item_email on user_import_item(lower(email));
