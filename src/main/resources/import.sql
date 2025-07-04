INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Rua das Flores, 123', 'Belo Horizonte', 'Minas Gerais', '30123-456', 'Brazil');

INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Av. Brasil, 456', 'São Paulo', 'São Paulo', '04567-890', 'Brazil');

INSERT INTO tb_address (street, city, state, postal_code, country)VALUES ('Praça da Sé, 789', 'Rio de Janeiro', 'Rio de Janeiro', '20000-000', 'Brazil');

INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Rua Nova, 101', 'Curitiba', 'Paraná', '80000-000', 'Brazil');

INSERT INTO tb_user (first_name, last_name, email, login, password, phone) VALUES ('Alex', 'Brown', 'alex@gmail.com', 'alex', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999111111');

INSERT INTO tb_user (first_name, last_name, email, login, password, phone) VALUES ('Bruno', 'Campos', 'bruno@gmail.com', 'bruno', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999222222');

INSERT INTO tb_user (first_name, last_name, email, login, password, phone) VALUES ('Maria', 'Clara', 'maria@gmail.com', 'maria', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999333333');

INSERT INTO tb_user (first_name, last_name, email, login, password, phone) VALUES ('Carlos', 'Santos', 'carlos@gmail.com', 'carlos', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999444444');

INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_role (authority) VALUES ('ROLE_EMPLOYEE');

INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');

INSERT INTO tb_role (authority) VALUES ('ROLE_USER');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 3);

INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 4);

UPDATE tb_user SET address_id = 1 WHERE id = 1;

UPDATE tb_user SET address_id = 2 WHERE id = 2;

UPDATE tb_user SET address_id = 3 WHERE id = 3;

UPDATE tb_user SET address_id = 4 WHERE id = 4;


INSERT INTO tb_room (description, price, image_url, is_active) VALUES ('Suíte Master com hidromassagem e vista panorâmica', 450.00, 'https://example.com/img1.jpg', true);

INSERT INTO tb_room (description, price, image_url, is_active) VALUES ('Quarto Standard com cama de casal', 250.00, 'https://example.com/img2.jpg', true);

INSERT INTO tb_room (description, price, image_url, is_active) VALUES ('Suíte Família com dois quartos conjugados', 600.00, 'https://example.com/img3.jpg', true);

INSERT INTO tb_room (description, price, image_url, is_active) VALUES ('Quarto Econômico com cama de solteiro', 150.00, 'https://example.com/img4.jpg', true);


INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (1, 1, '2025-06-01', '2025-06-02', 450.00);

INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (1, 2, '2025-06-10', '2025-06-12', 500.00);

INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (2, 1, '2025-06-15', '2025-06-16', 450.00);

INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (3, 3, '2025-06-20', '2025-06-21', 600.00);

INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (4, 2, '2025-06-25', '2025-06-26', 250.00);
