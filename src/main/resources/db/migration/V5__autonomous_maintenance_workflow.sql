ALTER TABLE public.autonomous_maintenance
    ADD COLUMN scheduled_for timestamp(6) without time zone,
    ADD COLUMN status character varying(50),
    ADD COLUMN coordinator_approver_user_id uuid,
    ADD COLUMN approved_at timestamp(6) without time zone,
    ADD COLUMN rejection_reason text,
    ADD COLUMN calendar_event_id uuid,
    ADD COLUMN created_at timestamp(6) without time zone,
    ADD COLUMN updated_at timestamp(6) without time zone;

CREATE TABLE public.autonomous_maintenance_students (
    autonomous_maintenance_id uuid NOT NULL,
    student_id uuid NOT NULL,
    CONSTRAINT pk_autonomous_maintenance_students
        PRIMARY KEY (autonomous_maintenance_id, student_id),
    CONSTRAINT fk_autonomous_maintenance_students_maintenance
        FOREIGN KEY (autonomous_maintenance_id)
        REFERENCES public.autonomous_maintenance(autonomous_maintenance_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_autonomous_maintenance_students_student
        FOREIGN KEY (student_id) REFERENCES public.student(user_id)
);

INSERT INTO public.autonomous_maintenance_students (
    autonomous_maintenance_id,
    student_id
)
SELECT autonomous_maintenance_id, responsible_student_id
FROM public.autonomous_maintenance
WHERE responsible_student_id IS NOT NULL
ON CONFLICT DO NOTHING;

UPDATE public.autonomous_maintenance
SET created_by_user_id = responsible_teacher_id
WHERE created_by_user_id IS NULL;

UPDATE public.autonomous_maintenance
SET scheduled_for = inspected_at,
    status = 'PENDENTE_APROVACAO_COORDENADOR',
    created_at = COALESCE(inspected_at, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    updated_at = CURRENT_TIMESTAMP AT TIME ZONE 'UTC';

ALTER TABLE public.autonomous_maintenance
    ALTER COLUMN inspected_at DROP NOT NULL,
    ALTER COLUMN created_by_user_id SET NOT NULL,
    ALTER COLUMN scheduled_for SET NOT NULL,
    ALTER COLUMN status SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE public.autonomous_maintenance
    DROP CONSTRAINT IF EXISTS fk_autonomous_maintenance_student_user,
    DROP COLUMN responsible_student_id;

ALTER TABLE public.autonomous_maintenance
    ADD CONSTRAINT autonomous_maintenance_status_check CHECK (
        status IN (
            'PENDENTE_APROVACAO_COORDENADOR',
            'APROVADA_PELO_COORDENADOR',
            'REPROVADA_PELO_COORDENADOR'
        )
    ),
    ADD CONSTRAINT fk_autonomous_maintenance_coordinator_approver
        FOREIGN KEY (coordinator_approver_user_id) REFERENCES public.users(user_id),
    ADD CONSTRAINT fk_autonomous_maintenance_calendar_event
        FOREIGN KEY (calendar_event_id) REFERENCES public.event(event_id),
    ADD CONSTRAINT uq_autonomous_maintenance_calendar_event UNIQUE (calendar_event_id);

ALTER TABLE public.event
    ALTER COLUMN equipment_id DROP NOT NULL;

ALTER TABLE public.event
    ADD CONSTRAINT event_equipment_required_check CHECK (
        maintenance_type = 'AUTONOMA' OR equipment_id IS NOT NULL
    );

CREATE INDEX idx_autonomous_maintenance_status
    ON public.autonomous_maintenance(status);

CREATE INDEX idx_autonomous_maintenance_created_by
    ON public.autonomous_maintenance(created_by_user_id);

CREATE INDEX idx_autonomous_maintenance_students_student
    ON public.autonomous_maintenance_students(student_id);
