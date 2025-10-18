CREATE TABLE user_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_date DATE NOT NULL,
    total DOUBLE NOT NULL
);