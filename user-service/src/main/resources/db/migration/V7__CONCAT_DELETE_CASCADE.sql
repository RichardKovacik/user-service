ALTER TABLE contact
    DROP CONSTRAINT fk_contact_on_user;

ALTER TABLE contact
    ADD CONSTRAINT fk_contact_on_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;