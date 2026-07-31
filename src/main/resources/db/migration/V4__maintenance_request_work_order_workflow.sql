ALTER TABLE public.maintenance_request
    DROP CONSTRAINT IF EXISTS maintenance_request_status_check;

ALTER TABLE public.maintenance_request
    ADD COLUMN IF NOT EXISTS work_order_number varchar(40),
    ADD COLUMN IF NOT EXISTS work_order_created_at timestamp without time zone,
    ADD COLUMN IF NOT EXISTS work_order_created_by_user_id uuid,
    ADD COLUMN IF NOT EXISTS coordinator_approved_by_user_id uuid,
    ADD COLUMN IF NOT EXISTS coordinator_approved_at timestamp without time zone,
    ADD COLUMN IF NOT EXISTS coordinator_rejection_reason text;

ALTER TABLE public.maintenance_request
    ADD CONSTRAINT maintenance_request_status_check CHECK (
        status IN (
            'PENDENTE_APROVACAO_PROFESSOR',
            'APROVADA_PELO_PROFESSOR',
            'REPROVADA_PELO_PROFESSOR',
            'PENDENTE_APROVACAO_COORDENADOR',
            'APROVADA_PELO_COORDENADOR',
            'REPROVADA_PELO_COORDENADOR',
            'NAO_VISUALIZADA',
            'FINALIZADA',
            'EM_ANALISE'
        )
    );

ALTER TABLE public.maintenance_request
    ADD CONSTRAINT uq_maintenance_request_work_order_number UNIQUE (work_order_number),
    ADD CONSTRAINT fk_maintenance_request_work_order_created_by
        FOREIGN KEY (work_order_created_by_user_id) REFERENCES public.users(user_id),
    ADD CONSTRAINT fk_maintenance_request_coordinator_approved_by
        FOREIGN KEY (coordinator_approved_by_user_id) REFERENCES public.users(user_id);
