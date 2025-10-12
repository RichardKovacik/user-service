package sk.mvp.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sk.mvp.user_service.model.Contact;
import sk.mvp.user_service.model.Gender;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUserAndContact() {
        Contact contact = new Contact("ferko@gmail.com", "421957884669");
        User user = new User("ferino", "heslo", contact, Gender.MALE);
        contact.setUser(user);

        userRepository.save(user);
        assertEquals(1, userRepository.count());
    }

}
