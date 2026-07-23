alter table media
    add column uploaded_by uuid,
    add column organization_id uuid,
    add column active boolean not null default true,
    add constraint fk_media_uploaded_by
        foreign key (uploaded_by) references users(user_id),
    add constraint fk_media_organization
        foreign key (organization_id) references organization(organization_id);

alter table users
    add column profile_photo_media_id uuid,
    add constraint uk_users_profile_photo unique (profile_photo_media_id),
    add constraint fk_users_profile_photo
        foreign key (profile_photo_media_id) references media(media_id);

create table notification_preference (
    notification_preference_id uuid primary key,
    user_id uuid not null,
    email_enabled boolean not null default true,
    in_app_enabled boolean not null default true,
    occurrence_notifications boolean not null default true,
    purchase_notifications boolean not null default true,
    inspection_notifications boolean not null default true,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    version bigint not null default 0,
    constraint uk_notification_preference_user unique (user_id),
    constraint fk_notification_preference_user
        foreign key (user_id) references users(user_id)
);

insert into notification_preference (
    notification_preference_id,
    user_id,
    email_enabled,
    in_app_enabled,
    occurrence_notifications,
    purchase_notifications,
    inspection_notifications,
    created_at,
    updated_at,
    version
)
select
    gen_random_uuid(),
    users.user_id,
    true,
    true,
    true,
    true,
    true,
    current_timestamp,
    current_timestamp,
    0
from users;

create index idx_media_uploaded_by on media(uploaded_by);
create index idx_media_organization on media(organization_id);
create index idx_media_active on media(active);
