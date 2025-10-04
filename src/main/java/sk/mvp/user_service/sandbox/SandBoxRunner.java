package sk.mvp.user_service.sandbox;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import sk.mvp.user_service.model.Role;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//@Component
public class SandBoxRunner implements CommandLineRunner {
    private UserRepository userRepository;
    private final org.springframework.context.ApplicationContext applicationContext;

    public SandBoxRunner(UserRepository userRepository, ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        System.out.println("===== REGISTERED BEANS =====");
        for (String beanName : beanNames) {
            System.out.println(beanName + " -> " + applicationContext.getBean(beanName).getClass().getName());
        }

//        List<User> users = userRepository.findAll().stream()
//                .filter(user -> user.getRoles() == null || user.getRoles().isEmpty())
//                        .toList();
//
//       for (User user : users) {
//           System.out.println(user.getUsername());
//       }
//       User user =  userRepository.findByUsername("grace").get();
//
//       Role role = new Role();
//       role.setName("ADMIN");
//       role.setId(2);
//
//       boolean isAdded = user.getRoles().add(role);
//        System.out.println(isAdded);
      // userRepository.save(user);

    }
}
