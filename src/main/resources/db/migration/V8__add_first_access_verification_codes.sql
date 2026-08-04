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
