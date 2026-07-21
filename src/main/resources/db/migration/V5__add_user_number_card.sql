-- Add a generated card number required by the User entity.
alter table users
    add column number_card varchar(255);

update users
set number_card = 'LEGACY-' || user_id
where number_card is null;

alter table users
    alter column number_card set not null;

alter table users
    add constraint uk_users_number_card unique (number_card);