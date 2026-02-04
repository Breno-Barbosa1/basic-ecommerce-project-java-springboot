ALTER TABLE user_orders
ADD COLUMN user_id BIGINT,
ADD CONSTRAINT fk_users
FOREIGN KEY (user_id)
REFERENCES users (id);