
INSERT INTO tb_user (name, email, login, password, phone) VALUES ('Alex', 'alex@gmail.com', 'alex', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999111111');
INSERT INTO tb_user (name, email, login, password, phone) VALUES ('Bruno', 'bruno@gmail.com', 'bruno', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999222222');
INSERT INTO tb_user (name, email, login, password, phone) VALUES ('Maria', 'maria@gmail.com', 'maria', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999333333');

INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_USER');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 3);