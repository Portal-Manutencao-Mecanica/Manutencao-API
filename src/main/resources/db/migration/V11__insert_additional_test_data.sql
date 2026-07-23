-- Dados adicionais para testar listagem, leitura, alternância e marcação em massa.
insert into notification (
    notification_id,
    number_card,
    notification_email,
    notification_title,
    notification_about,
    notification_description,
    notification_status_read
) values
    (8002, 'SEED-NOTIFICATION-8002', 'admin.teste@local.com',
     'Nova solicitação de manutenção', 'Solicitação',
     'Uma nova solicitação de manutenção aguarda análise.', false),
    (8003, 'SEED-NOTIFICATION-8003', 'coordenador.teste@local.com',
     'Compra aprovada', 'Compra',
     'A compra de materiais da oficina foi aprovada.', true),
    (8004, 'SEED-NOTIFICATION-8004', 'professor.teste@local.com',
     'Inspeção programada', 'Manutenção preventiva',
     'Uma inspeção preventiva foi programada para a próxima semana.', false),
    (8005, 'SEED-NOTIFICATION-8005', 'aluno.teste@local.com',
     'Ocorrência 5S registrada', '5S',
     'Uma nova ocorrência 5S foi registrada no laboratório elétrico.', false),
    (8006, 'SEED-NOTIFICATION-8006', 'professora.teste@local.com',
     'Relatório concluído', 'Log de máquina',
     'O relatório de execução do log de máquina foi concluído.', true)
on conflict do nothing;
