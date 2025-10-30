CREATE TABLE photos_presents
(
    id SERIAL PRIMARY KEY,
    present_id       BIGINT NOT NULL,
    photo_data       BYTEA NOT NULL,
    CONSTRAINT fk_photos_present FOREIGN KEY (present_id)
        REFERENCES presents (id)
        ON DELETE CASCADE
);