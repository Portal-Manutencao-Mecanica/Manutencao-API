ALTER TABLE public.maintenance_request
    DROP CONSTRAINT IF EXISTS maintenance_request_status_check;

ALTER TABLE public.maintenance_request
    ADD COLUMN IF NOT EXISTS approved_by_user_id uuid,
    ADD COLUMN IF NOT EXISTS approved_at timestamp without time zone,
    ADD COLUMN IF NOT EXISTS rejection_reason text;

ALTER TABLE public.maintenance_request
    ADD CONSTRAINT maintenance_request_status_check CHECK (
        status IN (
            'PENDENTE_APROVACAO_PROFESSOR',
            'APROVADA_PELO_PROFESSOR',
            'REPROVADA_PELO_PROFESSOR',
            'NAO_VISUALIZADA',
            'FINALIZADA',
            'EM_ANALISE'
        )
    );

ALTER TABLE public.maintenance_request
    ADD CONSTRAINT fk_maintenance_request_approved_by
    FOREIGN KEY (approved_by_user_id) REFERENCES public.users(user_id);