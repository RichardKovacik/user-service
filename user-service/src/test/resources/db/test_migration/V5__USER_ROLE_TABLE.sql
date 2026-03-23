CREATE TABLE user_role
(
    role_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (role_id, user_id)
);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_role FOREIGN KEY (role_id) REFERENCES role (id);

-- when user is has been deleted, it triggers cascade and remove also row from user_role table
ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
-- add all users default role USER

-- 1. Zisti ID roly USER (ak už existuje)
WITH role_cte AS (
    SELECT id AS role_id
    FROM role
    WHERE name = 'USER'
    LIMIT 1
)
-- 2. Vlož do user_role pre všetkých existujúcich používateľov
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.role_id
FROM users u
         CROSS JOIN role_cte r
-- aby sa predišlo duplicitám:
WHERE NOT EXISTS (
    SELECT 1
    FROM user_role ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.role_id
);