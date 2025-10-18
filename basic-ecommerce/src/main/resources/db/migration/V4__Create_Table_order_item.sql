CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantity BIGINT NOT NULL,
    price_at_purchase DECIMAL(10,2) NOT NULL
)