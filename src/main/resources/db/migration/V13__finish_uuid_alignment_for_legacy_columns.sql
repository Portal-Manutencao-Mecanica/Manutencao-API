alter table inconvenience_5s_student
    add column student_id_uuid_tmp uuid;

update inconvenience_5s_student association
   set student_id_uuid_tmp = users.user_id
  from users
  join student
    on student.user_id = users.user_id
 where users.number_card ~ '[0-9]+$'
   and substring(users.number_card from '([0-9]+)$')::bigint = association.student_id;

do
$$
begin
    if exists (
        select 1
        from inconvenience_5s_student
        where student_id_uuid_tmp is null
    ) then
        raise exception
            'UUID migration stopped: one or more inconvenience_5s_student rows could not be associated with a user. Correct the legacy number_card values and retry.';
    end if;
end
$$;

alter table inconvenience_5s_student
    drop constraint if exists inconvenience_5s_student_pkey;

alter table inconvenience_5s_student
    drop constraint if exists fk_inconvenience_5s_student_student;

alter table inconvenience_5s_student
    drop column student_id;

alter table inconvenience_5s_student
    rename column student_id_uuid_tmp to student_id;

alter table inconvenience_5s_student
    alter column student_id set not null;

alter table inconvenience_5s_student
    add constraint pk_inconvenience_5s_student
        primary key (inconvenience_5s_id, student_id);

alter table inconvenience_5s_student
    add constraint fk_inconvenience_5s_student_student
        foreign key (student_id)
        references student(user_id);

alter table buy
    drop column if exists buy_aluno_id,
    drop column if exists buy_professor_id,
    drop column if exists equipment_id;

alter table maintenance_request
    drop column if exists designation_id;

create index if not exists idx_inconvenience_5s_student_student
    on inconvenience_5s_student (student_id);
