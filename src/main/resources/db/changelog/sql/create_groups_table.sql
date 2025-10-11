CREATE TABLE groups
(
    id   SERIAL PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    teacher_id BIGINT       NOT NULL,
    CONSTRAINT fk_groups_teacher FOREIGN KEY (teacher_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);
