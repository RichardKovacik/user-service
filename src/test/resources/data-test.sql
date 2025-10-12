-- ===== INSERT ROLES =====
INSERT INTO role (name) VALUES ('ADMIN');
INSERT INTO role (name) VALUES ('USER');

-- ===== INSERT USERS =====
INSERT INTO users (first_name, last_name, username, password, gender, activated, activation_token)
VALUES
    ('Peter',    'Novak',     'pnovak',     'pass123', 'M', TRUE,  'token1'),
    ('Jana',     'Hruba',     'jhruba',     'pass123', 'F', TRUE,  'token2'),
    ('Marek',    'Kovac',     'mkovac',     'pass123', 'M', TRUE,  'token3'),
    ('Lucia',    'Siposova',  'lsiposova',  'pass123', 'F', TRUE,  'token4'),
    ('Tomas',    'Urban',     'turb',       'pass123', 'M', FALSE, 'token5'),
    ('Monika',   'Benova',    'mbenova',    'pass123', 'F', TRUE,  'token6'),
    ('Andrej',   'Kral',      'akral',      'pass123', 'M', TRUE,  'token7'),
    ('Simona',   'Vargova',   'svargova',   'pass123', 'F', TRUE,  'token8'),
    ('Filip',    'Toth',      'ftoth',      'pass123', 'M', TRUE,  'token9'),
    ('Eva',      'Polakova',  'epolakova',  'pass123', 'F', TRUE,  'token10');

-- ===== INSERT CONTACTS =====
INSERT INTO contact (user_id, email, phone_number)
VALUES
    (1, 'peter.novak@example.com',  '+421900111222'),
    (2, 'jana.hruba@example.com',   '+421900111333'),
    (3, 'marek.kovac@example.com',  '+421900111444'),
    (4, 'lucia.siposova@example.com', '+421900111555'),
    (5, 'tomas.urban@example.com',  '+421900111666'),
    (6, 'monika.benova@example.com','+421900111777'),
    (7, 'andrej.kral@example.com',  '+421900111888'),
    (8, 'simona.vargova@example.com','+421900111999'),
    (9, 'filip.toth@example.com',   '+421900111000'),
    (10, 'eva.polakova@example.com','+421900222333');

-- ===== ASSIGN ROLES TO USERS =====
-- ADMIN role for first 2 users
INSERT INTO user_role (role_id, user_id) VALUES (1, 1);
INSERT INTO user_role (role_id, user_id) VALUES (1, 2);

-- USER role for the rest
INSERT INTO user_role (role_id, user_id) VALUES (2, 3);
INSERT INTO user_role (role_id, user_id) VALUES (2, 4);
INSERT INTO user_role (role_id, user_id) VALUES (2, 5);
INSERT INTO user_role (role_id, user_id) VALUES (2, 6);
INSERT INTO user_role (role_id, user_id) VALUES (2, 7);
INSERT INTO user_role (role_id, user_id) VALUES (2, 8);
INSERT INTO user_role (role_id, user_id) VALUES (2, 9);
INSERT INTO user_role (role_id, user_id) VALUES (2, 10);