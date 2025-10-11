CREATE TABLE IF NOT EXISTS users
(
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    second_name VARCHAR(255) NOT NULL,
    patronymic_name VARCHAR(255),
    email VARCHAR(255),
    birth_date DATE,
    balance_coins BIGINT
);



