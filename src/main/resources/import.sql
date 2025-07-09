
INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Rua das Flores, 123', 'Belo Horizonte', 'Minas Gerais', '30123-456', 'Brazil');
INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Av. Brasil, 456', 'São Paulo', 'São Paulo', '04567-890', 'Brazil');
INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Praça da Sé, 789', 'Rio de Janeiro', 'Rio de Janeiro', '20000-000', 'Brazil');
INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Rua Nova, 101', 'Curitiba', 'Paraná', '80000-000', 'Brazil');

INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Rua do Sol, 55', 'Recife', 'Pernambuco', '50000-000', 'Brazil');
INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Av. Atlântica, 999', 'Salvador', 'Bahia', '40000-000', 'Brazil');
INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Rua das Palmeiras, 202', 'Florianópolis', 'Santa Catarina', '88000-000', 'Brazil');
INSERT INTO tb_address (street, city, state, postal_code, country) VALUES ('Alameda dos Anjos, 45', 'Porto Alegre', 'Rio Grande do Sul', '90000-000', 'Brazil');


INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Alex', 'Brown', 'alex@gmail.com', 'alex', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999111111', 1);
INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Bruno', 'Campos', 'bruno@gmail.com', 'bruno', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999222222', 2);
INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Maria', 'Clara', 'maria@gmail.com', 'maria', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999333333', 3);
INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Carlos', 'Santos', 'carlos@gmail.com', 'carlos', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '37999444444', 4);

INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Fernanda', 'Lima', 'fernanda@gmail.com', 'fernanda', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '31999888888', 5);
INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Diego', 'Silva', 'diego@gmail.com', 'diego', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '31999777777', 6);
INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Juliana', 'Alves', 'juliana@gmail.com', 'juliana', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '31999666666', 7);
INSERT INTO tb_user (first_name, last_name, email, login, password, phone, address_id) VALUES ('Ricardo', 'Mendes', 'ricardo@gmail.com', 'ricardo', '$2a$10$my3JjFgPUJjnNlbRyBPbkeDrXV3PobF821Js7wc/2QaCDHnmtevyW', '31999555555', 8);


INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_EMPLOYEE');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');


INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 3);

INSERT INTO tb_user_role (user_id, role_id) VALUES (5, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (6, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (7, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (8, 3);


INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Suíte Master com hidromassagem e vista panorâmica', 450.00, 'https://example.com/img1.jpg', true, NOW(), NOW());
INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Quarto Standard com cama de casal', 250.00, 'https://example.com/img2.jpg', true, NOW(), NOW());
INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Suíte Família com dois quartos conjugados', 600.00, 'https://example.com/img3.jpg', true, NOW(), NOW());
INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Quarto Econômico com cama de solteiro', 150.00, 'https://example.com/img4.jpg', true, NOW(), NOW());

INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Suíte Luxo com varanda e jacuzzi', 700.00, 'https://example.com/img5.jpg', true, NOW(), NOW());
INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Quarto Compacto com mesa de trabalho', 180.00, 'https://example.com/img6.jpg', true, NOW(), NOW());
INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Quarto com acessibilidade e cama queen', 300.00, 'https://example.com/img7.jpg', true, NOW(), NOW());
INSERT INTO tb_room (description, price, image_url, is_active, created_at, updated_at) VALUES ('Suíte Premium com decoração temática', 800.00, 'https://example.com/img8.jpg', true, NOW(), NOW());


INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (1, 1, '2025-06-01', '2025-06-02', 450.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (1, 2, '2025-06-10', '2025-06-12', 500.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (2, 1, '2025-06-15', '2025-06-16', 450.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (3, 3, '2025-06-20', '2025-06-21', 600.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (4, 2, '2025-06-25', '2025-06-26', 250.00);

INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (5, 5, '2025-07-10', '2025-07-12', 1400.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (6, 6, '2025-07-10', '2025-07-11', 180.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (7, 7, '2025-07-15', '2025-07-16', 300.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (8, 8, '2025-07-18', '2025-07-20', 1600.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (3, 6, '2025-07-21', '2025-07-22', 180.00);
INSERT INTO tb_stay (user_id, room_id, check_in, check_out, price) VALUES (2, 5, '2025-07-25', '2025-07-26', 700.00);
