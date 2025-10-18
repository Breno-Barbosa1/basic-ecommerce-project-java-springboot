ALTER TABLE order_item
ADD CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id)
    REFERENCES `user_orders` (id);