-- add new attribute username
ALTER TABLE users
    ADD username VARCHAR(255);

-- fill username default value from  email
UPDATE users u
SET username = SUBSTRING(c.email FROM 1 FOR 5)
FROM contact c
WHERE u.id = c.user_id
  AND u.username IS NULL;


ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE users
    ALTER COLUMN gender SET NOT NULL;