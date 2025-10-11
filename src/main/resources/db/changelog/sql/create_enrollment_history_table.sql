CREATE TABLE enrollment_history
(
    id      SERIAL PRIMARY KEY,
    teacher_id      BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    enrollment_date TIMESTAMP,
    enrolled_coins  BIGINT,
    CONSTRAINT fk_enrollment_teacher FOREIGN KEY (teacher_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);