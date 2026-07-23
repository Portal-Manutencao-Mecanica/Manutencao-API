-- Dados de teste compartilhados.
-- Senha dos usuarios de teste: password

insert into users (
    user_id, user_name, user_email, user_password, user_role,
    user_enabled, account_non_locked, created_at, updated_at, number_card
) values
    (1001, 'Administrador Teste', 'admin.teste@local.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true, true, timestamp '2026-01-15 08:00:00', timestamp '2026-01-15 08:00:00', 'CARD-TEST-1001'),
    (1002, 'Coordenador Teste', 'coordenador.teste@local.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'COORDENADOR', true, true, timestamp '2026-01-15 08:00:00', timestamp '2026-01-15 08:00:00', 'CARD-TEST-1002'),
    (1003, 'Professor Teste', 'professor.teste@local.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR', true, true, timestamp '2026-01-15 08:00:00', timestamp '2026-01-15 08:00:00', 'CARD-TEST-1003'),
    (1004, 'Professora Teste', 'professora.teste@local.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR', true, true, timestamp '2026-01-15 08:00:00', timestamp '2026-01-15 08:00:00', 'CARD-TEST-1004'),
    (1005, 'Aluno Teste', 'aluno.teste@local.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO', true, true, timestamp '2026-01-15 08:00:00', timestamp '2026-01-15 08:00:00', 'CARD-TEST-1005'),
    (1006, 'Aluna Teste', 'aluna.teste@local.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO', true, true, timestamp '2026-01-15 08:00:00', timestamp '2026-01-15 08:00:00', 'CARD-TEST-1006')
on conflict do nothing;

insert into admin (user_id) values (1001) on conflict do nothing;
insert into coordinator (user_id) values (1002) on conflict do nothing;
insert into teacher (user_id) values (1003), (1004) on conflict do nothing;
insert into student (user_id) values (1005), (1006) on conflict do nothing;

insert into class_group (acronym)
select seed.acronym
from (values ('TEST-MEC-01'), ('TEST-MEC-02'), ('TEST-ELE-01')) as seed(acronym)
where not exists (select 1 from class_group existing where existing.acronym = seed.acronym);

insert into class_group_teacher (class_group_id, teacher_id)
select cg.class_group_id, u.user_id
from class_group cg cross join users u
where cg.acronym = 'TEST-MEC-01' and u.user_email = 'professor.teste@local.com'
  and not exists (select 1 from class_group_teacher x where x.class_group_id = cg.class_group_id and x.teacher_id = u.user_id);

insert into class_group_teacher (class_group_id, teacher_id)
select cg.class_group_id, u.user_id
from class_group cg cross join users u
where cg.acronym = 'TEST-ELE-01' and u.user_email = 'professora.teste@local.com'
  and not exists (select 1 from class_group_teacher x where x.class_group_id = cg.class_group_id and x.teacher_id = u.user_id);

insert into class_group_student (class_group_id, student_id)
select cg.class_group_id, u.user_id
from class_group cg cross join users u
where cg.acronym = 'TEST-MEC-01' and u.user_email in ('aluno.teste@local.com', 'aluna.teste@local.com')
  and not exists (select 1 from class_group_student x where x.class_group_id = cg.class_group_id and x.student_id = u.user_id);

insert into place (place_id, place_name) values
    (2001, 'Oficina Mecnica'),
    (2002, 'Laboratrio Eltrico')
on conflict do nothing;

insert into equipment (equipment_id, equipment_name, equipment_sap, unit_price, available_quantity) values
    (3001, 'Kit de ferramentas de manuteno', 'SAP-TEST-3001', 1250.00, 10),
    (3002, 'Multmetro digital', 'SAP-TEST-3002', 480.00, 5)
on conflict do nothing;

insert into machine (machine_id, machine_name, machine_patrimony, machine_condition, machine_tag, place_id, created_at) values
    (4001, 'Torno mecnico de teste', 'PAT-TEST-4001', 'CONFORME', 'TAG-TEST-4001', 2001, timestamp '2026-01-15 08:30:00'),
    (4002, 'Bancada eltrica de teste', 'PAT-TEST-4002', 'NAO_CONFORME', 'TAG-TEST-4002', 2002, timestamp '2026-01-15 08:30:00')
on conflict do nothing;

insert into designation (designation_id, designation_sector) values
    (5001, 'WEG_MANUTENCAO'),
    (5002, 'CENTRO_WEG')
on conflict do nothing;

insert into helper_material (helper_material_id, title, description, url, type) values
    (6001, 'Manual de manuteno preventiva', 'Material de apoio para manuteno preventiva.', 'https://example.com/manual-preventiva', 'MANUAL'),
    (6002, 'Tabela de lubrificao', 'Tabela de periodicidade e tipos de lubrificante.', 'https://example.com/lubrificacao', 'LUBRIFICACAO')
on conflict do nothing;

insert into media (media_id, media_description, media_type, storage_key, original_name, content_type, file_size, created_at) values
    (7001, 'Imagem de equipamento de teste', 'EQUIPMENT', 'seed/test-equipment-7001.jpg', 'equipment-7001.jpg', 'image/jpeg', 2048, timestamp '2026-01-15 09:00:00')
on conflict do nothing;

insert into equipment_media (equipment_id, media_id)
select 3001, 7001
where not exists (select 1 from equipment_media where equipment_id = 3001 and media_id = 7001);

insert into notification (notification_id, notification_email, notification_title, notification_about, notification_description, notification_status_read) values
    (8001, 'professor.teste@local.com', 'Manuteno pendente', 'Mquina', 'Existe uma manuteno pendente para anlise.', false)
on conflict do nothing;

insert into event (event_id, scheduled_action, criticality, created_at, scheduled_for, requested_at, student_id, teacher_id, equipment_id, machine_id, place_id, maintenance_type, status)
select 9001, 'Verificar alinhamento e lubrificao do torno.', 'MEDIA', timestamp '2026-01-15 09:00:00', timestamp '2026-01-20 10:00:00', timestamp '2026-01-15 09:00:00', 1005, 1003, 3001, 4001, 2001, 'PREVENTIVA', 'PENDENTE'
where not exists (select 1 from event where event_id = 9001);

insert into buy (buy_id, status, created_by_user_id, notified_teacher_id, purchase_justification, class_group_id, created_at)
select 10001, 'EM_ANALISE', 1001, 1003, 'Compra de ferramentas para a oficina.', cg.class_group_id, timestamp '2026-01-15 09:30:00'
from class_group cg
where cg.acronym = 'TEST-MEC-01'
  and not exists (select 1 from buy where buy_id = 10001);

insert into buy_item (buy_item_id, buy_id, equipment_id, quantity, technical_specification, sap, patrimony, tag, mechanical_set)
select 11001, 10001, 3002, 2, 'Multmetro com medio de tenso, corrente e resistncia.', 'SAP-TEST-3002', 'PAT-TEST-11001', 'TAG-TEST-11001', 'Eltrica'
where exists (select 1 from buy where buy_id = 10001)
  and not exists (select 1 from buy_item where buy_item_id = 11001);

insert into machine_log (machine_log_id, title, description, execution_report, task_situation, machine_id, service_performed, registered_at, planned_action, task_criticality, place_id, maintenance_type, class_group_id, report_link, created_by_user_id, responsible_teacher_id)
select 12001, 'Inspeo inicial', 'Registro de inspeo para teste.', 'Nenhuma anomalia crtica identificada.', 'CONCLUIDA', 4001, 'Inspeo visual e lubrificao.', timestamp '2026-01-15 10:00:00', 'Revisar pontos de lubrificao.', 'BAIXA', 2001, 'PREVENTIVA', cg.class_group_id, 'https://example.com/report-12001', 1001, 1003
from class_group cg
where cg.acronym = 'TEST-MEC-01'
  and not exists (select 1 from machine_log where machine_log_id = 12001);

insert into machine_log_student (machine_log_id, student_id)
select 12001, seed.student_id from (values (1005), (1006)) as seed(student_id)
where not exists (select 1 from machine_log_student x where x.machine_log_id = 12001 and x.student_id = seed.student_id);

insert into autonomous_maintenance (autonomous_maintenance_id, equipment_situation, inspected_at, inspected_machine_id, equipment_condition, identified_nonconformities, responsible_teacher_id, responsible_student_id, created_by_user_id)
values (13001, 'OPERANDO', timestamp '2026-01-15 11:00:00', 4001, 'CONFORME', 'Nenhuma nao conformidade encontrada.', 1003, 1005, 1001)
on conflict do nothing;

insert into inconvenience_5s (inconvenience_5s_id, inconvenience, status, place_id, notified_teacher_id, created_at, class_group_id, created_by_user_id, description, registration_period)
select 14001, 'Material fora do local identificado.', 'NAO_VISUALIZADA', 2001, 1003, timestamp '2026-01-15 12:00:00', cg.class_group_id, 1005, 'Ferramentas deixadas sobre a bancada apos a atividade.', 'MATUTINO'
from class_group cg
where cg.acronym = 'TEST-MEC-01'
  and not exists (select 1 from inconvenience_5s where inconvenience_5s_id = 14001);

insert into inconvenience_5s_student (inconvenience_5s_id, student_id)
select 14001, seed.student_id from (values (1005), (1006)) as seed(student_id)
where not exists (select 1 from inconvenience_5s_student x where x.inconvenience_5s_id = 14001 and x.student_id = seed.student_id);

insert into maintenance_request (maintenance_request_id, status, designation_id, sector, priority, created_by_user_id, place_id, description, created_at, notified_teacher_id, machine_id)
values (15001, 'NAO_VISUALIZADA', 5001, 'WEG_MANUTENCAO', 'ALTA', 1005, 2002, 'A bancada eletrica apresenta falha intermitente.', timestamp '2026-01-15 13:00:00', 1004, 4002)
on conflict do nothing;

insert into maintenance_request_student (maintenance_request_id, student_id)
select 15001, seed.student_id from (values (1005), (1006)) as seed(student_id)
where not exists (select 1 from maintenance_request_student x where x.maintenance_request_id = 15001 and x.student_id = seed.student_id);

insert into history_log (history_log_id, actor_id, actor_role, action, entity_type, entity_id, description, created_at)
values (16001, 1001, 'ADMIN', 'CREATED', 'MAINTENANCE_REQUEST', 15001, 'Solicitacao de teste criada.', timestamp '2026-01-15 13:00:00')
on conflict do nothing;
insert into media (media_id, media_description, media_type, storage_key, original_name, content_type, file_size, created_at) values
    (7002, 'Anexo da compra de teste', 'PURCHASE_REQUEST', 'seed/test-purchase-7002.pdf', 'purchase-7002.pdf', 'application/pdf', 4096, timestamp '2026-01-15 09:00:00'),
    (7003, 'Relatorio do log de maquina', 'MACHINE_LOG', 'seed/test-machine-log-7003.pdf', 'machine-log-7003.pdf', 'application/pdf', 4096, timestamp '2026-01-15 10:00:00'),
    (7004, 'Foto da solicitacao de manutencao', 'MAINTENANCE_REQUEST', 'seed/test-maintenance-7004.jpg', 'maintenance-7004.jpg', 'image/jpeg', 2048, timestamp '2026-01-15 13:00:00'),
    (7005, 'Foto da inspecao autonoma', 'AUTONOMOUS_MAINTENANCE', 'seed/test-autonomous-7005.jpg', 'autonomous-7005.jpg', 'image/jpeg', 2048, timestamp '2026-01-15 11:00:00'),
    (7006, 'Foto da ocorrencia 5S', 'INCONVENIENCE_5S', 'seed/test-5s-7006.jpg', '5s-7006.jpg', 'image/jpeg', 2048, timestamp '2026-01-15 12:00:00')
on conflict do nothing;

insert into buy_media (buy_id, media_id)
select 10001, 7002
where not exists (select 1 from buy_media x where x.buy_id = 10001 and x.media_id = 7002);

insert into machine_log_media (machine_log_id, media_id)
select 12001, 7003
where not exists (select 1 from machine_log_media x where x.machine_log_id = 12001 and x.media_id = 7003);

insert into maintenance_request_media (maintenance_request_id, media_id)
select 15001, 7004
where not exists (select 1 from maintenance_request_media x where x.maintenance_request_id = 15001 and x.media_id = 7004);

insert into autonomous_maintenance_media (autonomous_maintenance_id, media_id)
select 13001, 7005
where not exists (select 1 from autonomous_maintenance_media x where x.autonomous_maintenance_id = 13001 and x.media_id = 7005);

insert into inconvenience_5s_media (inconvenience_5s_id, media_id)
select 14001, 7006
where not exists (select 1 from inconvenience_5s_media x where x.inconvenience_5s_id = 14001 and x.media_id = 7006);