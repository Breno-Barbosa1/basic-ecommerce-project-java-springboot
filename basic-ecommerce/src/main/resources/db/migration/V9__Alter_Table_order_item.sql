ALTER TABLE order_item
ADD CONSTRAINT fk_order_item_product
    FOREIGN KEY (product_id)
    REFERENCES `products` (id);