-- Insert 10 users
INSERT INTO users (first_name, last_name, password, gender, activated, activation_token)
VALUES ('John', 'Doe', 'password123', 'M', TRUE, 'token1'),
       ('Jane', 'Smith', 'password123', 'F', TRUE, 'token2'),
       ('Alice', 'Johnson', 'password123', 'F', FALSE, 'token3'),
       ('Bob', 'Brown', 'password123', 'M', TRUE, 'token4'),
       ('Charlie', 'Davis', 'password123', 'M', FALSE, 'token5'),
       ('Diana', 'Miller', 'password123', 'F', TRUE, 'token6'),
       ('Eve', 'Wilson', 'password123', 'F', FALSE, 'token7'),
       ('Frank', 'Moore', 'password123', 'M', TRUE, 'token8'),
       ('Grace', 'Taylor', 'password123', 'F', TRUE, 'token9'),
       ('Henry', 'Anderson', 'password123', 'M', FALSE, 'token10');

-- Insert matching contacts
INSERT INTO contact (user_id, email, phone_number)
VALUES (1, 'john.doe@example.com', '+421900111111'),
       (2, 'jane.smith@example.com', '+421900111112'),
       (3, 'alice.johnson@example.com', '+421900111113'),
       (4, 'bob.brown@example.com', '+421900111114'),
       (5, 'charlie.davis@example.com', '+421900111115'),
       (6, 'diana.miller@example.com', '+421900111116'),
       (7, 'eve.wilson@example.com', '+421900111117'),
       (8, 'frank.moore@example.com', '+421900111118'),
       (9, 'grace.taylor@example.com', '+421900111119'),
       (10, 'henry.anderson@example.com', '+421900111120');