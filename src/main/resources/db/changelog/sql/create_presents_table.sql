CREATE TABLE presents
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    prise_coins BIGINT       NOT NULL,
    stock       INT          NOT NULL
);