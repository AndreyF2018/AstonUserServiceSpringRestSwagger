-- Упрощенная версия для TestContainers
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    age INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Тестовые данные (опционально)
INSERT INTO users (id, name, email, age, created_at) VALUES
(1, 'alexey', 'alexey@example.com', 35, '2024-05-12 16:45:00'),
(2, 'Елена', 'elena@example.com', 27, '2024-06-18 13:10:00'),
(3, 'Дмитрий', 'dmitry@example.com', 31, '2024-07-22 08:30:00');

-- Функция для поиска по email (если используется в UserDao)
CREATE OR REPLACE FUNCTION find_user_by_email(input_email VARCHAR)
RETURNS SETOF users AS $$
BEGIN
    RETURN QUERY SELECT * FROM users WHERE email = input_email;
END;
$$ LANGUAGE plpgsql;