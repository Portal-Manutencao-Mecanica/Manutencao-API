alter table users
    add column status_change_reason varchar(500),
    add column status_changed_at timestamp(6),
    add column status_changed_by uuid,
    add column security_version bigint not null default 0;

alter table users
    add constraint fk_users_status_changed_by
        foreign key (status_changed_by)
        references users(user_id)
        on delete set null;

create index idx_users_status_changed_by
    on users(status_changed_by);
