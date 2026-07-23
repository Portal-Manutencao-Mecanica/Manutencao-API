create table refresh_token (
    refresh_token_id uuid primary key,
    user_id uuid not null,
    token_hash varchar(64) not null,
    expires_at timestamp(6) not null,
    revoked_at timestamp(6),
    created_at timestamp(6) not null,
    ip_address varchar(64),
    user_agent varchar(500),
    constraint uk_refresh_token_hash unique (token_hash),
    constraint fk_refresh_token_user
        foreign key (user_id)
        references users(user_id)
);

create index idx_refresh_token_user
    on refresh_token (user_id);

create index idx_refresh_token_expires_at
    on refresh_token (expires_at);

create index idx_refresh_token_active_user
    on refresh_token (user_id, expires_at)
    where revoked_at is null;
