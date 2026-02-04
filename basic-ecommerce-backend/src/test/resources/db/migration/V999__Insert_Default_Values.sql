INSERT INTO users (id, email, password, address) VALUES
    (4, "breno@gmail.com", "{pbkdf2}b98ee97c341fca247016e6a4a1f669288cd11e7a86247ca61ffad4b3b4d85249a7e5f7093e710b8b", "Campina Grande - Brazil");

INSERT INTO user_permission (user_id , permission_id) VALUES
    (4, 1);

INSERT INTO products (id , name, description, price, stock_quantity) VALUES
    (1, "Intel Pc", "Intel Powered Pc", 1000, 100);