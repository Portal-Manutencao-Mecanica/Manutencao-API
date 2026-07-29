-- Contas de teste destinadas aos ambientes de desenvolvimento e homologacao.
-- Senha de todas as contas: Senha@123

DO $$
BEGIN
IF '${seed_test_users}'::boolean THEN

INSERT INTO public.organization (
    organization_id,
    name,
    type,
    email_domain,
    active,
    created_at,
    updated_at,
    version
)
VALUES (
    'f0a00000-0000-4000-8000-000000000001',
    'Organizacao de Teste',
    'OTHER',
    'teste.local',
    true,
    CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
    CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
    0
)
ON CONFLICT (email_domain) DO NOTHING;

INSERT INTO public.users (
    user_id,
    user_name,
    username,
    user_email,
    user_password,
    user_role,
    user_enabled,
    account_non_locked,
    organization_id,
    password_change_required,
    password_changed_at,
    failed_login_attempts,
    lockout_count,
    security_version,
    created_at,
    updated_at,
    number_card,
    version
)
VALUES
    (
        'f0a00000-0000-4000-8000-000000000011',
        'Administrador de Teste',
        'admin.teste',
        'admin@teste.local',
        '$2a$10$JKv5TAh.Rl/VqHtKKoITvuaJDRfCpHPeAmYKH47Yhazm8OVIOeLp6',
        'ADMIN',
        true,
        true,
        (SELECT organization_id FROM public.organization WHERE email_domain = 'teste.local'),
        false,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        0,
        0,
        0,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        'TESTE-ADMIN-001',
        0
    ),
    (
        'f0a00000-0000-4000-8000-000000000012',
        'Coordenador de Teste',
        'coordenador.teste',
        'coordenador@teste.local',
        '$2a$10$JKv5TAh.Rl/VqHtKKoITvuaJDRfCpHPeAmYKH47Yhazm8OVIOeLp6',
        'COORDENADOR',
        true,
        true,
        (SELECT organization_id FROM public.organization WHERE email_domain = 'teste.local'),
        false,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        0,
        0,
        0,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        'TESTE-COORDENADOR-001',
        0
    ),
    (
        'f0a00000-0000-4000-8000-000000000013',
        'Professor de Teste',
        'professor.teste',
        'professor@teste.local',
        '$2a$10$JKv5TAh.Rl/VqHtKKoITvuaJDRfCpHPeAmYKH47Yhazm8OVIOeLp6',
        'PROFESSOR',
        true,
        true,
        (SELECT organization_id FROM public.organization WHERE email_domain = 'teste.local'),
        false,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        0,
        0,
        0,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        'TESTE-PROFESSOR-001',
        0
    ),
    (
        'f0a00000-0000-4000-8000-000000000014',
        'Aluno de Teste',
        'aluno.teste',
        'aluno@teste.local',
        '$2a$10$JKv5TAh.Rl/VqHtKKoITvuaJDRfCpHPeAmYKH47Yhazm8OVIOeLp6',
        'ALUNO',
        true,
        true,
        (SELECT organization_id FROM public.organization WHERE email_domain = 'teste.local'),
        false,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        0,
        0,
        0,
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        CURRENT_TIMESTAMP AT TIME ZONE 'UTC',
        'TESTE-ALUNO-001',
        0
    )
ON CONFLICT (user_email) DO NOTHING;

INSERT INTO public.admin (user_id)
SELECT user_id
FROM public.users
WHERE user_email = 'admin@teste.local'
  AND user_role = 'ADMIN'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO public.coordinator (user_id)
SELECT user_id
FROM public.users
WHERE user_email = 'coordenador@teste.local'
  AND user_role = 'COORDENADOR'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO public.teacher (user_id)
SELECT user_id
FROM public.users
WHERE user_email = 'professor@teste.local'
  AND user_role = 'PROFESSOR'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO public.student (user_id)
SELECT user_id
FROM public.users
WHERE user_email = 'aluno@teste.local'
  AND user_role = 'ALUNO'
ON CONFLICT (user_id) DO NOTHING;

END IF;
END $$;
