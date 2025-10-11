CREATE TABLE photos_presents
(
    id SERIAL PRIMARY KEY,
    present_id       BIGINT NOT NULL,
    photo            BYTEA,
    CONSTRAINT fk_photos_present FOREIGN KEY (present_id)
        REFERENCES presents (id)
        ON DELETE CASCADE
);