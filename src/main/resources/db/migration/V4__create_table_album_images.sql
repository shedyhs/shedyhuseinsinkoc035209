CREATE TABLE album_images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    album_id UUID NOT NULL REFERENCES albums(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
