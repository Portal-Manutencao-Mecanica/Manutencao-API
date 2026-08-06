ALTER TABLE public.machine_log
    ADD COLUMN IF NOT EXISTS maintenance_request_id uuid;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM pg_constraint
         WHERE conname = 'fk_machine_log_maintenance_request'
    ) THEN
        ALTER TABLE ONLY public.machine_log
            ADD CONSTRAINT fk_machine_log_maintenance_request
            FOREIGN KEY (maintenance_request_id)
            REFERENCES public.maintenance_request(maintenance_request_id);
    END IF;
END
$$;
