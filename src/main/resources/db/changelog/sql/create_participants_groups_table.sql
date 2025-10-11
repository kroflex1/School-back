CREATE TABLE participants_groups
(
    user_id  BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, group_id),
    CONSTRAINT fk_participants_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_participants_group FOREIGN KEY (group_id)
        REFERENCES groups (id)
        ON DELETE CASCADE
);