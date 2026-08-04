ALTER TABLE public.media
    DROP CONSTRAINT IF EXISTS media_storage_key_key;

ALTER TABLE public.media
    ALTER COLUMN storage_key TYPE TEXT;
