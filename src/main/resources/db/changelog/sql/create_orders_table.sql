CREATE TABLE orders
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT      NOT NULL,
    present_id   BIGINT      NOT NULL,
    order_date   TIMESTAMP   NOT NULL,
    order_status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_orders_present FOREIGN KEY (present_id)
        REFERENCES presents (id)
        ON DELETE CASCADE
)