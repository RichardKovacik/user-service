package sk.mvp.user_service.sandbox;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sk.mvp.user_service.model.Role;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.repository.UserRepository;

import java.util.Optional;

//@Component
public class SandBoxRunner implements CommandLineRunner {
    private UserRepository userRepository;

    public SandBoxRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
       User user =  userRepository.findByUsername("grace").get();

       Role role = new Role();
       role.setName("ADMIN");
       role.setId(2);

       boolean isAdded = user.getRoles().add(role);
        System.out.println(isAdded);
      // userRepository.save(user);

    }
}
