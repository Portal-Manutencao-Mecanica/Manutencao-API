-- Schema consolidado e vazio. Dados de desenvolvimento pertencem somente ao seed manual externo.
-- Seed: C:\Users\vinicius_lopes150\Downloads\seed_massivo_weg_senai.sql

create extension if not exists pgcrypto;

CREATE TABLE public.admin (
    user_id uuid NOT NULL
);



CREATE TABLE public.audit_log (
    audit_log_id uuid NOT NULL,
    user_id uuid,
    username character varying(150),
    action character varying(80) NOT NULL,
    entity_type character varying(80),
    entity_id uuid,
    endpoint character varying(255),
    http_method character varying(10),
    ip_address character varying(64),
    user_agent character varying(500),
    created_at timestamp(6) without time zone NOT NULL,
    success boolean NOT NULL,
    details text
);

CREATE TABLE public.autonomous_maintenance (
    inspected_at timestamp(6) without time zone NOT NULL,
    equipment_condition character varying(255) NOT NULL,
    equipment_situation character varying(255) NOT NULL,
    identified_nonconformities text,
    patrimony character varying(255),
    tag character varying(255),
    autonomous_maintenance_id uuid NOT NULL,
    created_by_user_id uuid,
    inspected_machine_id uuid NOT NULL,
    responsible_student_id uuid NOT NULL,
    responsible_teacher_id uuid NOT NULL,
    CONSTRAINT autonomous_maintenance_equipment_condition_check CHECK (((equipment_condition)::text = ANY ((ARRAY['CONFORME'::character varying, 'NAO_CONFORME'::character varying])::text[]))),
    CONSTRAINT autonomous_maintenance_equipment_situation_check CHECK (((equipment_situation)::text = ANY ((ARRAY['OPERANDO'::character varying, 'NAO_OPERANDO'::character varying])::text[])))
);



CREATE TABLE public.autonomous_maintenance_media (
    autonomous_maintenance_id uuid NOT NULL,
    media_id uuid NOT NULL
);



CREATE TABLE public.buy (
    created_at timestamp(6) without time zone,
    buy_quantity integer,
    buy_mechanical_set character varying(255),
    buy_patrimony character varying(255),
    purchase_justification character varying(255),
    buy_sap character varying(255),
    status_legacy character varying(255),
    buy_tag character varying(255),
    buy_technical_specification character varying(255),
    status character varying(30) NOT NULL,
    buy_id uuid NOT NULL,
    class_group_id uuid,
    created_by_user_id uuid,
    notified_teacher_id uuid,
    CONSTRAINT buy_buy_status_check CHECK (((status_legacy)::text = ANY ((ARRAY['Entregue'::character varying, 'EmAnalise'::character varying, 'Pedideemandamento'::character varying, 'Naovisualizado'::character varying])::text[])))
);



CREATE TABLE public.buy_item (
    quantity integer NOT NULL,
    technical_specification text,
    sap character varying(100),
    patrimony character varying(100),
    tag character varying(100),
    mechanical_set character varying(150),
    buy_item_id uuid NOT NULL,
    buy_id uuid NOT NULL,
    equipment_id uuid NOT NULL
);



CREATE TABLE public.buy_media (
    buy_id uuid NOT NULL,
    media_id uuid NOT NULL
);



CREATE TABLE public.buy_media_files (
    media_file character varying(255),
    buy_id uuid NOT NULL
);



CREATE TABLE public.class_group (
    created_at_user timestamp(6) without time zone,
    acronym character varying(255),
    enabled boolean DEFAULT true NOT NULL,
    class_group_id uuid NOT NULL
);



CREATE TABLE public.class_group_student (
    class_group_id uuid NOT NULL,
    student_id uuid NOT NULL
);



CREATE TABLE public.class_group_teacher (
    class_group_id uuid NOT NULL,
    teacher_id uuid NOT NULL
);



CREATE TABLE public.coordinator (
    user_id uuid NOT NULL
);



CREATE TABLE public.designation (
    designation_sector character varying(255),
    designation_id uuid NOT NULL,
    CONSTRAINT designation_designation_sector_check CHECK (((designation_sector)::text = ANY ((ARRAY['AREA_NAO_DESIGNADA'::character varying, 'CENTRO_WEG'::character varying, 'WEG_MANUTENCAO'::character varying])::text[])))
);



CREATE TABLE public.equipment (
    unit_price numeric(15,2),
    available_quantity integer,
    equipment_sap character varying(255),
    equipment_name character varying(255),
    equipment_id uuid NOT NULL
);



CREATE TABLE public.equipment_media (
    equipment_id uuid NOT NULL,
    media_id uuid NOT NULL
);



CREATE TABLE public.event (
    scheduled_action text NOT NULL,
    criticality character varying(30) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    scheduled_for timestamp(6) without time zone NOT NULL,
    requested_at timestamp(6) without time zone NOT NULL,
    maintenance_type character varying(30) NOT NULL,
    status character varying(30) NOT NULL,
    event_id uuid NOT NULL,
    equipment_id uuid NOT NULL,
    machine_id uuid NOT NULL,
    place_id uuid NOT NULL,
    student_id uuid,
    teacher_id uuid NOT NULL
);



CREATE TABLE public.helper_material (
    helper_material_link_lubrification character varying(255),
    helper_material_link_manual character varying(255),
    helper_material_link_prevent_maintance character varying(255),
    helper_material_link_tecnic character varying(255),
    title character varying(150) NOT NULL,
    description text,
    url character varying(2048) NOT NULL,
    type character varying(40) NOT NULL,
    helper_material_id uuid NOT NULL
);



CREATE TABLE public.history_log (
    actor_role character varying(30) NOT NULL,
    action character varying(40) NOT NULL,
    entity_type character varying(40) NOT NULL,
    description text,
    created_at timestamp(6) without time zone NOT NULL,
    entity_id uuid NOT NULL,
    history_log_id uuid NOT NULL,
    actor_id uuid NOT NULL
);



CREATE TABLE public.inconvenience_5s (
    inconvenience character varying(255) NOT NULL,
    status character varying(30) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    registered_occasion character varying(255),
    registration_period character varying(30) NOT NULL,
    inconvenience_5s_id uuid NOT NULL,
    class_group_id uuid NOT NULL,
    created_by_user_id uuid,
    place_id uuid NOT NULL,
    notified_teacher_id uuid NOT NULL
);



CREATE TABLE public.inconvenience_5s_image (
    image_url character varying(2048) NOT NULL,
    inconvenience_5s_id uuid NOT NULL
);



CREATE TABLE public.inconvenience_5s_media (
    inconvenience_5s_id uuid NOT NULL,
    media_id uuid NOT NULL
);



CREATE TABLE public.inconvenience_5s_student (
    inconvenience_5s_id uuid NOT NULL,
    student_id uuid NOT NULL
);



CREATE TABLE public.machine (
    created_at timestamp(6) without time zone,
    machine_patrimony character varying(255),
    machine_name character varying(255),
    machine_condition character varying(255),
    machine_tag character varying(255),
    machine_last_maintenance timestamp(6) without time zone,
    machine_id uuid NOT NULL,
    place_id uuid
);



CREATE TABLE public.machine_log (
    teacher_concluded_at timestamp(6) without time zone,
    registered_at timestamp(6) without time zone,
    description character varying(255),
    service_performed character varying(255),
    title character varying(255),
    execution_report text,
    task_situation character varying(255),
    planned_action text,
    task_criticality character varying(255),
    image bytea,
    maintenance_type character varying(255),
    registration_period character varying(255),
    report_link character varying(2048),
    execution_started_at timestamp(6) without time zone,
    execution_ended_at timestamp(6) without time zone,
    machine_log_id uuid NOT NULL,
    class_group_id uuid,
    created_by_user_id uuid,
    machine_id uuid,
    place_id uuid,
    responsible_teacher_id uuid
);



CREATE TABLE public.machine_log_media (
    machine_log_id uuid NOT NULL,
    media_id uuid NOT NULL
);



CREATE TABLE public.machine_log_student (
    machine_log_id uuid NOT NULL,
    student_id uuid NOT NULL
);



CREATE TABLE public.maintenance_request (
    status character varying(255) NOT NULL,
    priority character varying(255) NOT NULL,
    description text NOT NULL,
    patrimony character varying(255),
    tag character varying(255),
    created_at timestamp(6) without time zone NOT NULL,
    sector character varying(30) NOT NULL,
    maintenance_request_id uuid NOT NULL,
    created_by_user_id uuid,
    machine_id uuid NOT NULL,
    place_id uuid NOT NULL,
    notified_teacher_id uuid NOT NULL,
    CONSTRAINT maintenance_request_priority_check CHECK (((priority)::text = ANY ((ARRAY['ALTA'::character varying, 'MEDIA'::character varying, 'BAIXA'::character varying])::text[]))),
    CONSTRAINT maintenance_request_status_check CHECK (((status)::text = ANY ((ARRAY['NAO_VISUALIZADA'::character varying, 'FINALIZADA'::character varying, 'EM_ANALISE'::character varying])::text[])))
);



CREATE TABLE public.maintenance_request_image (
    image_data bytea NOT NULL,
    maintenance_request_id uuid NOT NULL
);



CREATE TABLE public.maintenance_request_media (
    maintenance_request_id uuid NOT NULL,
    media_id uuid NOT NULL
);



CREATE TABLE public.maintenance_request_student (
    maintenance_request_id uuid NOT NULL,
    student_id uuid NOT NULL
);



CREATE TABLE public.media (
    media_description character varying(255),
    media_type character varying(50) NOT NULL,
    storage_key character varying(500) NOT NULL,
    original_name character varying(255) NOT NULL,
    content_type character varying(100) NOT NULL,
    file_size bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    media_id uuid NOT NULL,
    uploaded_by uuid,
    organization_id uuid,
    active boolean DEFAULT true NOT NULL
);



CREATE TABLE public.notification (
    notification_email character varying(150) NOT NULL,
    notification_title character varying(150) NOT NULL,
    notification_about character varying(255),
    notification_description text NOT NULL,
    notification_status_read boolean DEFAULT false NOT NULL,
    notification_id uuid NOT NULL
);



CREATE TABLE public.notification_preference (
    notification_preference_id uuid NOT NULL,
    user_id uuid NOT NULL,
    email_enabled boolean DEFAULT true NOT NULL,
    in_app_enabled boolean DEFAULT true NOT NULL,
    occurrence_notifications boolean DEFAULT true NOT NULL,
    purchase_notifications boolean DEFAULT true NOT NULL,
    inspection_notifications boolean DEFAULT true NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint DEFAULT 0 NOT NULL
);



CREATE TABLE public.organization (
    organization_id uuid NOT NULL,
    name character varying(150) NOT NULL,
    type character varying(30) NOT NULL,
    email_domain character varying(150) NOT NULL,
    active boolean DEFAULT true NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT ck_organization_type CHECK (((type)::text = ANY ((ARRAY['SENAI'::character varying, 'WEG'::character varying, 'OTHER'::character varying])::text[])))
);



CREATE TABLE public.password_reset_token (
    password_reset_token_id uuid NOT NULL,
    user_id uuid NOT NULL,
    token_hash character varying(64) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    expires_at timestamp(6) without time zone NOT NULL,
    used_at timestamp(6) without time zone,
    requested_ip character varying(64)
);



CREATE TABLE public.place (
    place_name character varying(255),
    place_id uuid NOT NULL
);



CREATE TABLE public.refresh_token (
    refresh_token_id uuid NOT NULL,
    user_id uuid NOT NULL,
    token_hash character varying(64) NOT NULL,
    expires_at timestamp(6) without time zone NOT NULL,
    revoked_at timestamp(6) without time zone,
    created_at timestamp(6) without time zone NOT NULL,
    ip_address character varying(64),
    user_agent character varying(500)
);



CREATE TABLE public.student (
    user_id uuid NOT NULL
);



CREATE TABLE public.teacher (
    user_id uuid NOT NULL
);



CREATE TABLE public.user_import (
    user_import_id uuid NOT NULL,
    filename character varying(255) NOT NULL,
    imported_by uuid NOT NULL,
    organization_id uuid,
    status character varying(40) NOT NULL,
    total_rows integer DEFAULT 0 NOT NULL,
    created_count integer DEFAULT 0 NOT NULL,
    failed_count integer DEFAULT 0 NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    completed_at timestamp(6) without time zone,
    CONSTRAINT ck_user_import_counts CHECK (((total_rows >= 0) AND (created_count >= 0) AND (failed_count >= 0) AND ((created_count + failed_count) <= total_rows))),
    CONSTRAINT ck_user_import_status CHECK (((status)::text = ANY ((ARRAY['PROCESSING'::character varying, 'COMPLETED'::character varying, 'COMPLETED_WITH_ERRORS'::character varying, 'FAILED'::character varying])::text[])))
);



CREATE TABLE public.user_import_item (
    user_import_item_id uuid NOT NULL,
    user_import_id uuid NOT NULL,
    row_number integer NOT NULL,
    name character varying(150),
    username character varying(50),
    email character varying(150),
    role character varying(30),
    organization_value character varying(150),
    status character varying(20) NOT NULL,
    created_user_id uuid,
    error_code character varying(80),
    error_field character varying(80),
    error_message character varying(500),
    created_at timestamp(6) without time zone NOT NULL,
    CONSTRAINT ck_user_import_item_result CHECK (((((status)::text = 'CREATED'::text) AND (created_user_id IS NOT NULL) AND (error_code IS NULL)) OR (((status)::text = 'FAILED'::text) AND (created_user_id IS NULL) AND (error_code IS NOT NULL)))),
    CONSTRAINT ck_user_import_item_role CHECK (((role IS NULL) OR ((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'ALUNO'::character varying, 'PROFESSOR'::character varying, 'COORDENADOR'::character varying])::text[])))),
    CONSTRAINT ck_user_import_item_status CHECK (((status)::text = ANY ((ARRAY['CREATED'::character varying, 'FAILED'::character varying])::text[])))
);



CREATE TABLE public.users (
    user_name character varying(150) NOT NULL,
    user_email character varying(150) NOT NULL,
    user_password character varying(100) NOT NULL,
    user_role character varying(30) NOT NULL,
    user_enabled boolean DEFAULT true NOT NULL,
    account_non_locked boolean DEFAULT true NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    number_card character varying(255) NOT NULL,
    user_id uuid NOT NULL,
    username character varying(50) NOT NULL,
    organization_id uuid NOT NULL,
    password_change_required boolean DEFAULT false NOT NULL,
    temporary_password_expires_at timestamp(6) without time zone,
    password_changed_at timestamp(6) without time zone,
    failed_login_attempts integer DEFAULT 0 NOT NULL,
    locked_until timestamp(6) without time zone,
    last_failed_login_at timestamp(6) without time zone,
    lockout_count integer DEFAULT 0 NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    status_change_reason character varying(500),
    status_changed_at timestamp(6) without time zone,
    status_changed_by uuid,
    security_version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT ck_users_failed_login_attempts CHECK ((failed_login_attempts >= 0)),
    CONSTRAINT ck_users_lockout_count CHECK ((lockout_count >= 0))
);



ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (user_id);



ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (audit_log_id);



ALTER TABLE ONLY public.autonomous_maintenance_media
    ADD CONSTRAINT autonomous_maintenance_media_media_id_key UNIQUE (media_id);



ALTER TABLE ONLY public.autonomous_maintenance_media
    ADD CONSTRAINT autonomous_maintenance_media_pkey PRIMARY KEY (autonomous_maintenance_id, media_id);



ALTER TABLE ONLY public.autonomous_maintenance
    ADD CONSTRAINT autonomous_maintenance_pkey PRIMARY KEY (autonomous_maintenance_id);



ALTER TABLE ONLY public.buy_item
    ADD CONSTRAINT buy_item_pkey PRIMARY KEY (buy_item_id);



ALTER TABLE ONLY public.buy_media
    ADD CONSTRAINT buy_media_media_id_key UNIQUE (media_id);



ALTER TABLE ONLY public.buy_media
    ADD CONSTRAINT buy_media_pkey PRIMARY KEY (buy_id, media_id);



ALTER TABLE ONLY public.buy
    ADD CONSTRAINT buy_pkey PRIMARY KEY (buy_id);



ALTER TABLE ONLY public.class_group
    ADD CONSTRAINT class_pkey PRIMARY KEY (class_group_id);



ALTER TABLE ONLY public.coordinator
    ADD CONSTRAINT coordinator_pkey PRIMARY KEY (user_id);



ALTER TABLE ONLY public.designation
    ADD CONSTRAINT designation_pkey PRIMARY KEY (designation_id);



ALTER TABLE ONLY public.equipment_media
    ADD CONSTRAINT equipment_media_media_id_key UNIQUE (media_id);



ALTER TABLE ONLY public.equipment_media
    ADD CONSTRAINT equipment_media_pkey PRIMARY KEY (equipment_id, media_id);



ALTER TABLE ONLY public.equipment
    ADD CONSTRAINT equipment_pkey PRIMARY KEY (equipment_id);



ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (event_id);



ALTER TABLE ONLY public.helper_material
    ADD CONSTRAINT helper_material_pkey PRIMARY KEY (helper_material_id);



ALTER TABLE ONLY public.history_log
    ADD CONSTRAINT history_log_pkey PRIMARY KEY (history_log_id);



ALTER TABLE ONLY public.inconvenience_5s_media
    ADD CONSTRAINT inconvenience_5s_media_media_id_key UNIQUE (media_id);



ALTER TABLE ONLY public.inconvenience_5s_media
    ADD CONSTRAINT inconvenience_5s_media_pkey PRIMARY KEY (inconvenience_5s_id, media_id);



ALTER TABLE ONLY public.inconvenience_5s
    ADD CONSTRAINT inconvenience_5s_pkey PRIMARY KEY (inconvenience_5s_id);



ALTER TABLE ONLY public.machine_log_media
    ADD CONSTRAINT machine_log_media_media_id_key UNIQUE (media_id);



ALTER TABLE ONLY public.machine_log_media
    ADD CONSTRAINT machine_log_media_pkey PRIMARY KEY (machine_log_id, media_id);



ALTER TABLE ONLY public.machine_log
    ADD CONSTRAINT machine_log_pkey PRIMARY KEY (machine_log_id);



ALTER TABLE ONLY public.machine
    ADD CONSTRAINT machine_pkey PRIMARY KEY (machine_id);



ALTER TABLE ONLY public.maintenance_request_media
    ADD CONSTRAINT maintenance_request_media_media_id_key UNIQUE (media_id);



ALTER TABLE ONLY public.maintenance_request_media
    ADD CONSTRAINT maintenance_request_media_pkey PRIMARY KEY (maintenance_request_id, media_id);



ALTER TABLE ONLY public.maintenance_request
    ADD CONSTRAINT maintenance_request_pkey PRIMARY KEY (maintenance_request_id);



ALTER TABLE ONLY public.media
    ADD CONSTRAINT media_pkey PRIMARY KEY (media_id);



ALTER TABLE ONLY public.media
    ADD CONSTRAINT media_storage_key_key UNIQUE (storage_key);



ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (notification_id);



ALTER TABLE ONLY public.notification_preference
    ADD CONSTRAINT notification_preference_pkey PRIMARY KEY (notification_preference_id);



ALTER TABLE ONLY public.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (organization_id);



ALTER TABLE ONLY public.password_reset_token
    ADD CONSTRAINT password_reset_token_pkey PRIMARY KEY (password_reset_token_id);



ALTER TABLE ONLY public.inconvenience_5s_student
    ADD CONSTRAINT pk_inconvenience_5s_student PRIMARY KEY (inconvenience_5s_id, student_id);



ALTER TABLE ONLY public.place
    ADD CONSTRAINT place_pkey PRIMARY KEY (place_id);



ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT refresh_token_pkey PRIMARY KEY (refresh_token_id);



ALTER TABLE ONLY public.student
    ADD CONSTRAINT student_pkey PRIMARY KEY (user_id);



ALTER TABLE ONLY public.teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (user_id);



ALTER TABLE ONLY public.notification_preference
    ADD CONSTRAINT uk_notification_preference_user UNIQUE (user_id);



ALTER TABLE ONLY public.organization
    ADD CONSTRAINT uk_organization_email_domain UNIQUE (email_domain);



ALTER TABLE ONLY public.organization
    ADD CONSTRAINT uk_organization_name UNIQUE (name);



ALTER TABLE ONLY public.password_reset_token
    ADD CONSTRAINT uk_password_reset_token_hash UNIQUE (token_hash);



ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT uk_refresh_token_hash UNIQUE (token_hash);



ALTER TABLE ONLY public.user_import_item
    ADD CONSTRAINT uk_user_import_item_row UNIQUE (user_import_id, row_number);







ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_users_username UNIQUE (username);



ALTER TABLE ONLY public.user_import_item
    ADD CONSTRAINT user_import_item_pkey PRIMARY KEY (user_import_item_id);



ALTER TABLE ONLY public.user_import
    ADD CONSTRAINT user_import_pkey PRIMARY KEY (user_import_id);



ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);



ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_user_email_key UNIQUE (user_email);



CREATE INDEX idx_audit_log_action ON public.audit_log USING btree (action);



CREATE INDEX idx_audit_log_created_at ON public.audit_log USING btree (created_at DESC);



CREATE INDEX idx_audit_log_entity ON public.audit_log USING btree (entity_type, entity_id);



CREATE INDEX idx_audit_log_ip ON public.audit_log USING btree (ip_address);



CREATE INDEX idx_audit_log_user ON public.audit_log USING btree (user_id);



CREATE INDEX idx_event_equipment ON public.event USING btree (equipment_id);



CREATE INDEX idx_event_machine ON public.event USING btree (machine_id);



CREATE INDEX idx_event_place ON public.event USING btree (place_id);



CREATE INDEX idx_event_student ON public.event USING btree (student_id);



CREATE INDEX idx_event_teacher ON public.event USING btree (teacher_id);



CREATE INDEX idx_history_log_actor ON public.history_log USING btree (actor_id);



CREATE INDEX idx_history_log_entity ON public.history_log USING btree (entity_type, entity_id);



CREATE INDEX idx_inconvenience_5s_student_student ON public.inconvenience_5s_student USING btree (student_id);



CREATE INDEX idx_media_active ON public.media USING btree (active);



CREATE INDEX idx_media_organization ON public.media USING btree (organization_id);



CREATE INDEX idx_media_uploaded_by ON public.media USING btree (uploaded_by);



CREATE INDEX idx_notification_email ON public.notification USING btree (notification_email);



CREATE INDEX idx_notification_status_read ON public.notification USING btree (notification_status_read);



CREATE INDEX idx_password_reset_token_active_user ON public.password_reset_token USING btree (user_id, expires_at) WHERE (used_at IS NULL);



CREATE INDEX idx_password_reset_token_expires_at ON public.password_reset_token USING btree (expires_at);



CREATE INDEX idx_password_reset_token_user ON public.password_reset_token USING btree (user_id);



CREATE INDEX idx_refresh_token_active_user ON public.refresh_token USING btree (user_id, expires_at) WHERE (revoked_at IS NULL);



CREATE INDEX idx_refresh_token_expires_at ON public.refresh_token USING btree (expires_at);



CREATE INDEX idx_refresh_token_user ON public.refresh_token USING btree (user_id);



CREATE INDEX idx_user_import_actor ON public.user_import USING btree (imported_by);



CREATE INDEX idx_user_import_created_at ON public.user_import USING btree (created_at DESC);



CREATE INDEX idx_user_import_item_email ON public.user_import_item USING btree (lower((email)::text));



CREATE INDEX idx_user_import_item_import ON public.user_import_item USING btree (user_import_id, row_number);



CREATE INDEX idx_user_import_organization ON public.user_import USING btree (organization_id);



CREATE INDEX idx_users_email ON public.users USING btree (lower((user_email)::text));



CREATE INDEX idx_users_locked_until ON public.users USING btree (locked_until) WHERE (locked_until IS NOT NULL);



CREATE INDEX idx_users_organization ON public.users USING btree (organization_id);



CREATE INDEX idx_users_status_changed_by ON public.users USING btree (status_changed_by);



















































CREATE UNIQUE INDEX uk_users_email_lower ON public.users USING btree (lower((user_email)::text));



ALTER TABLE ONLY public.admin
    ADD CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.autonomous_maintenance
    ADD CONSTRAINT fk_autonomous_maintenance_created_by FOREIGN KEY (created_by_user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.autonomous_maintenance
    ADD CONSTRAINT fk_autonomous_maintenance_machine FOREIGN KEY (inspected_machine_id) REFERENCES public.machine(machine_id);



ALTER TABLE ONLY public.autonomous_maintenance_media
    ADD CONSTRAINT fk_autonomous_maintenance_media_maintenance FOREIGN KEY (autonomous_maintenance_id) REFERENCES public.autonomous_maintenance(autonomous_maintenance_id);



ALTER TABLE ONLY public.autonomous_maintenance_media
    ADD CONSTRAINT fk_autonomous_maintenance_media_media FOREIGN KEY (media_id) REFERENCES public.media(media_id);



ALTER TABLE ONLY public.autonomous_maintenance
    ADD CONSTRAINT fk_autonomous_maintenance_student_user FOREIGN KEY (responsible_student_id) REFERENCES public.student(user_id);



ALTER TABLE ONLY public.autonomous_maintenance
    ADD CONSTRAINT fk_autonomous_maintenance_teacher_user FOREIGN KEY (responsible_teacher_id) REFERENCES public.teacher(user_id);



ALTER TABLE ONLY public.buy
    ADD CONSTRAINT fk_buy_class_group_new FOREIGN KEY (class_group_id) REFERENCES public.class_group(class_group_id);



ALTER TABLE ONLY public.buy
    ADD CONSTRAINT fk_buy_created_by FOREIGN KEY (created_by_user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.buy_item
    ADD CONSTRAINT fk_buy_item_buy FOREIGN KEY (buy_id) REFERENCES public.buy(buy_id);



ALTER TABLE ONLY public.buy_item
    ADD CONSTRAINT fk_buy_item_equipment FOREIGN KEY (equipment_id) REFERENCES public.equipment(equipment_id);



ALTER TABLE ONLY public.buy_media
    ADD CONSTRAINT fk_buy_media_buy FOREIGN KEY (buy_id) REFERENCES public.buy(buy_id);



ALTER TABLE ONLY public.buy_media_files
    ADD CONSTRAINT fk_buy_media_files_buy FOREIGN KEY (buy_id) REFERENCES public.buy(buy_id);



ALTER TABLE ONLY public.buy_media
    ADD CONSTRAINT fk_buy_media_media FOREIGN KEY (media_id) REFERENCES public.media(media_id);



ALTER TABLE ONLY public.buy
    ADD CONSTRAINT fk_buy_notified_teacher FOREIGN KEY (notified_teacher_id) REFERENCES public.teacher(user_id);



ALTER TABLE ONLY public.class_group_student
    ADD CONSTRAINT fk_class_group_student_student FOREIGN KEY (student_id) REFERENCES public.student(user_id);



ALTER TABLE ONLY public.class_group_teacher
    ADD CONSTRAINT fk_class_group_teacher_teacher FOREIGN KEY (teacher_id) REFERENCES public.teacher(user_id);



ALTER TABLE ONLY public.class_group_student
    ADD CONSTRAINT fk_class_students_class FOREIGN KEY (class_group_id) REFERENCES public.class_group(class_group_id);



ALTER TABLE ONLY public.class_group_teacher
    ADD CONSTRAINT fk_class_teachers_class FOREIGN KEY (class_group_id) REFERENCES public.class_group(class_group_id);



ALTER TABLE ONLY public.coordinator
    ADD CONSTRAINT fk_coordinator_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.equipment_media
    ADD CONSTRAINT fk_equipment_media_equipment FOREIGN KEY (equipment_id) REFERENCES public.equipment(equipment_id);



ALTER TABLE ONLY public.equipment_media
    ADD CONSTRAINT fk_equipment_media_media FOREIGN KEY (media_id) REFERENCES public.media(media_id);



ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_equipment FOREIGN KEY (equipment_id) REFERENCES public.equipment(equipment_id);



ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_machine FOREIGN KEY (machine_id) REFERENCES public.machine(machine_id);



ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_place FOREIGN KEY (place_id) REFERENCES public.place(place_id);



ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_student FOREIGN KEY (student_id) REFERENCES public.student(user_id);



ALTER TABLE ONLY public.event
    ADD CONSTRAINT fk_event_teacher FOREIGN KEY (teacher_id) REFERENCES public.teacher(user_id);



ALTER TABLE ONLY public.history_log
    ADD CONSTRAINT fk_history_log_actor FOREIGN KEY (actor_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.inconvenience_5s
    ADD CONSTRAINT fk_inconvenience_5s_class_group_new FOREIGN KEY (class_group_id) REFERENCES public.class_group(class_group_id);



ALTER TABLE ONLY public.inconvenience_5s
    ADD CONSTRAINT fk_inconvenience_5s_created_by FOREIGN KEY (created_by_user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.inconvenience_5s_image
    ADD CONSTRAINT fk_inconvenience_5s_image FOREIGN KEY (inconvenience_5s_id) REFERENCES public.inconvenience_5s(inconvenience_5s_id);



ALTER TABLE ONLY public.inconvenience_5s_media
    ADD CONSTRAINT fk_inconvenience_5s_media_inconvenience FOREIGN KEY (inconvenience_5s_id) REFERENCES public.inconvenience_5s(inconvenience_5s_id);



ALTER TABLE ONLY public.inconvenience_5s_media
    ADD CONSTRAINT fk_inconvenience_5s_media_media FOREIGN KEY (media_id) REFERENCES public.media(media_id);



ALTER TABLE ONLY public.inconvenience_5s
    ADD CONSTRAINT fk_inconvenience_5s_place_new FOREIGN KEY (place_id) REFERENCES public.place(place_id);



ALTER TABLE ONLY public.inconvenience_5s_student
    ADD CONSTRAINT fk_inconvenience_5s_student_5s FOREIGN KEY (inconvenience_5s_id) REFERENCES public.inconvenience_5s(inconvenience_5s_id);



ALTER TABLE ONLY public.inconvenience_5s_student
    ADD CONSTRAINT fk_inconvenience_5s_student_student FOREIGN KEY (student_id) REFERENCES public.student(user_id);



ALTER TABLE ONLY public.inconvenience_5s
    ADD CONSTRAINT fk_inconvenience_5s_teacher_user FOREIGN KEY (notified_teacher_id) REFERENCES public.teacher(user_id);



ALTER TABLE ONLY public.machine_log
    ADD CONSTRAINT fk_machine_log_class_group FOREIGN KEY (class_group_id) REFERENCES public.class_group(class_group_id);



ALTER TABLE ONLY public.machine_log
    ADD CONSTRAINT fk_machine_log_created_by FOREIGN KEY (created_by_user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.machine_log
    ADD CONSTRAINT fk_machine_log_machine FOREIGN KEY (machine_id) REFERENCES public.machine(machine_id);



ALTER TABLE ONLY public.machine_log_media
    ADD CONSTRAINT fk_machine_log_media_log FOREIGN KEY (machine_log_id) REFERENCES public.machine_log(machine_log_id);



ALTER TABLE ONLY public.machine_log_media
    ADD CONSTRAINT fk_machine_log_media_media FOREIGN KEY (media_id) REFERENCES public.media(media_id);



ALTER TABLE ONLY public.machine_log
    ADD CONSTRAINT fk_machine_log_place FOREIGN KEY (place_id) REFERENCES public.place(place_id);



ALTER TABLE ONLY public.machine_log
    ADD CONSTRAINT fk_machine_log_responsible_teacher FOREIGN KEY (responsible_teacher_id) REFERENCES public.teacher(user_id);



ALTER TABLE ONLY public.machine_log_student
    ADD CONSTRAINT fk_machine_log_student_log FOREIGN KEY (machine_log_id) REFERENCES public.machine_log(machine_log_id);



ALTER TABLE ONLY public.machine_log_student
    ADD CONSTRAINT fk_machine_log_student_student FOREIGN KEY (student_id) REFERENCES public.student(user_id);



ALTER TABLE ONLY public.machine
    ADD CONSTRAINT fk_machine_place FOREIGN KEY (place_id) REFERENCES public.place(place_id);



ALTER TABLE ONLY public.maintenance_request
    ADD CONSTRAINT fk_maintenance_request_created_by FOREIGN KEY (created_by_user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.maintenance_request_image
    ADD CONSTRAINT fk_maintenance_request_image FOREIGN KEY (maintenance_request_id) REFERENCES public.maintenance_request(maintenance_request_id);



ALTER TABLE ONLY public.maintenance_request
    ADD CONSTRAINT fk_maintenance_request_machine_new FOREIGN KEY (machine_id) REFERENCES public.machine(machine_id);



ALTER TABLE ONLY public.maintenance_request_media
    ADD CONSTRAINT fk_maintenance_request_media_media FOREIGN KEY (media_id) REFERENCES public.media(media_id);



ALTER TABLE ONLY public.maintenance_request_media
    ADD CONSTRAINT fk_maintenance_request_media_request FOREIGN KEY (maintenance_request_id) REFERENCES public.maintenance_request(maintenance_request_id);



ALTER TABLE ONLY public.maintenance_request
    ADD CONSTRAINT fk_maintenance_request_place_new FOREIGN KEY (place_id) REFERENCES public.place(place_id);



ALTER TABLE ONLY public.maintenance_request_student
    ADD CONSTRAINT fk_maintenance_request_student_request FOREIGN KEY (maintenance_request_id) REFERENCES public.maintenance_request(maintenance_request_id);



ALTER TABLE ONLY public.maintenance_request_student
    ADD CONSTRAINT fk_maintenance_request_student_student FOREIGN KEY (student_id) REFERENCES public.student(user_id);



ALTER TABLE ONLY public.maintenance_request
    ADD CONSTRAINT fk_maintenance_request_teacher_user FOREIGN KEY (notified_teacher_id) REFERENCES public.teacher(user_id);



ALTER TABLE ONLY public.media
    ADD CONSTRAINT fk_media_organization FOREIGN KEY (organization_id) REFERENCES public.organization(organization_id);



ALTER TABLE ONLY public.media
    ADD CONSTRAINT fk_media_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.notification_preference
    ADD CONSTRAINT fk_notification_preference_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.password_reset_token
    ADD CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.student
    ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.teacher
    ADD CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.user_import
    ADD CONSTRAINT fk_user_import_actor FOREIGN KEY (imported_by) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.user_import_item
    ADD CONSTRAINT fk_user_import_item_created_user FOREIGN KEY (created_user_id) REFERENCES public.users(user_id);



ALTER TABLE ONLY public.user_import_item
    ADD CONSTRAINT fk_user_import_item_import FOREIGN KEY (user_import_id) REFERENCES public.user_import(user_import_id);



ALTER TABLE ONLY public.user_import
    ADD CONSTRAINT fk_user_import_organization FOREIGN KEY (organization_id) REFERENCES public.organization(organization_id);



ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_users_organization FOREIGN KEY (organization_id) REFERENCES public.organization(organization_id);




ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_users_status_changed_by FOREIGN KEY (status_changed_by) REFERENCES public.users(user_id) ON DELETE SET NULL;






do $$
declare
    table_name text;
    tables text[] := array[
        'autonomous_maintenance', 'buy', 'buy_item', 'class_group',
        'designation', 'equipment', 'event', 'helper_material', 'history_log',
        'inconvenience_5s', 'machine', 'machine_log', 'maintenance_request',
        'media', 'notification', 'place'
    ];
begin
    foreach table_name in array tables loop
    end loop;
end
$$;

-- Conteudo consolidado de V2__maintenance_request_teacher_approval.sql (sem DML)
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

-- Conteudo consolidado de V4__maintenance_request_work_order_workflow.sql (sem DML)
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

-- Conteudo consolidado de V5__autonomous_maintenance_workflow.sql (sem DML)
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

-- Conteudo consolidado de V6__allow_maintenance_request_images.sql (sem DML)
ALTER TABLE public.media
    DROP CONSTRAINT IF EXISTS media_storage_key_key;

ALTER TABLE public.media
    ALTER COLUMN storage_key TYPE TEXT;

-- Conteudo consolidado de V7__add_equipment_identifiers.sql (sem DML)
ALTER TABLE public.equipment
    ADD COLUMN equipment_patrimony character varying(100),
    ADD COLUMN equipment_tag character varying(100);

-- Conteudo consolidado de V8__add_first_access_verification_codes.sql (sem DML)
CREATE TABLE first_access_code (
    first_access_code_id uuid NOT NULL,
    user_id uuid NOT NULL,
    code_hash character varying(100) NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    expires_at timestamp(6) without time zone NOT NULL,
    used_at timestamp(6) without time zone,
    CONSTRAINT first_access_code_pkey PRIMARY KEY (first_access_code_id),
    CONSTRAINT fk_first_access_code_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_first_access_code_active_user
    ON first_access_code (user_id, created_at DESC)
    WHERE used_at IS NULL;

CREATE INDEX idx_first_access_code_expires_at
    ON first_access_code (expires_at);

-- Conteudo consolidado de V9__add_machine_image.sql (sem DML)
ALTER TABLE machine
    ADD COLUMN machine_image text;
