CREATE TABLE user_permission (
    user_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    PRIMARY KEY(user_id, permission_id),
    KEY fk_user_permission_permission (permission_id),
    CONSTRAINT fk_user_permission FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_permission_permission FOREIGN KEY (permission_id) REFERENCES permission (id)
);