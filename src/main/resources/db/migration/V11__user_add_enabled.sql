ALTER TABLE users
    ADD enabled BOOLEAN;

UPDATE users
SET enabled = 'true'
WHERE enabled IS NULL;
ALTER TABLE users
    ALTER COLUMN enabled SET NOT NULL;
