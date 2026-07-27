create table password_reset_token (
    password_reset_token_id uuid primary key,
    user_id uuid not null,
    token_hash varchar(64) not null,
    created_at timestamp(6) not null,
    expires_at timestamp(6) not null,
    used_at timestamp(6),
    requested_ip varchar(64),
    constraint uk_password_reset_token_hash unique (token_hash),
    constraint fk_password_reset_token_user
        foreign key (user_id) references users(user_id)
);

create index idx_password_reset_token_user
    on password_reset_token(user_id);

create index idx_password_reset_token_expires_at
    on password_reset_token(expires_at);

create index idx_password_reset_token_active_user
    on password_reset_token(user_id, expires_at)
    where used_at is null;
