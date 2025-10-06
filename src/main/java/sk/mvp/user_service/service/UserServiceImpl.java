package sk.mvp.user_service.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.dto.ErrorType;
import sk.mvp.user_service.dto.user.UserCreateDTO;
import sk.mvp.user_service.dto.user.UserProfileDTO;
import sk.mvp.user_service.dto.user.UserSummaryDTO;
import sk.mvp.user_service.exception.ApplicationException;
import sk.mvp.user_service.model.Contact;
import sk.mvp.user_service.model.Gender;
import sk.mvp.user_service.model.Role;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.projections.UserSummaryProjection;
import sk.mvp.user_service.repository.RoleRepository;
import sk.mvp.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public UserProfileDTO getUserByFirstName(String firstName) {
        User user = userRepository.findByFirstName(firstName).orElseThrow(() -> new ApplicationException(
                String.format("User with firstName %s not found",
                firstName),
                ErrorType.USER_NOT_FOUND, null));
        return new UserProfileDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ApplicationException("User with email " + email + " not found", ErrorType.USER_NOT_FOUND, null));
        return new UserProfileDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApplicationException("User with username " + username + " not found", ErrorType.USER_NOT_FOUND, null));
        return new UserProfileDTO(user);
    }

    @Override
    public List<UserSummaryDTO> getUsers(int page, int rows) {
       Page<UserSummaryProjection> users = userRepository.findAllProjectedBy(PageRequest.of(page, rows));
       if (users.isEmpty()){
           return List.of();
       }
       return users.getContent()
               .stream()
               .map(UserSummaryDTO::new)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserProfileDTO saveUser(UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null) {
            throw new IllegalArgumentException("Registration user data cannot be null");
        }
        //TODO: 2.check if it is email in good pattern using validator, regex ?? Bean validator
        //1. check if email already exists
        checkEmailIsNotUsed(userCreateDTO.getEmail());

        // create new instance of user
        Contact contact = new Contact(userCreateDTO.getEmail());
        User user = new User(userCreateDTO.getUsername(),
                userCreateDTO.getPassword(),
                contact,
                Gender.getValidGenderFromCode(userCreateDTO.getGenderCodeAsCharacter()));
        //set user to new contact
        contact.setUser(user);
        // save user to DB
        User savedUser = userRepository.save(user);

        return new UserProfileDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteUserbyUsername(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        User user = userRepository.findByUsername(userName).orElseThrow(() ->
                new ApplicationException("User with username " + userName + " not found", ErrorType.USER_NOT_FOUND, null));
        userRepository.delete(user);
        //TODO: log user has been deleted
    }

    @Override
    public void assignRoleToUser(String username, String roleName) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (roleName == null || roleName.isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApplicationException("User with username " + username + " not found", ErrorType.USER_NOT_FOUND, null));

        Role role = roleRepository.findByName(roleName).orElseThrow(
                () -> new ApplicationException("Role with name " + roleName + " not found", ErrorType.ROLE_NOT_FOUND, null));

        if (user.getRoles().contains(role)) {
            throw new ApplicationException(String.format("User %s already has %s role", username, roleName), ErrorType.ROLE_ALREADY_ASSIGNED, null);
        }

        user.getRoles().add(role);

        userRepository.save(user);
    }

    @Override
    public void unassignRoleFromUser(String username, String roleName) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (roleName == null || roleName.isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApplicationException("User with username " + username + " not found", ErrorType.USER_NOT_FOUND, null));

        Role role = roleRepository.findByName(roleName).orElseThrow(
                () -> new ApplicationException("Role with name " + roleName + "not found", ErrorType.ROLE_NOT_FOUND, null));

        if (!user.getRoles().contains(role)) {
            throw new RuntimeException("User does not have this role");
        }
        user.getRoles().remove(role);

        userRepository.save(user);
    }

    @Override
    public List<UserSummaryDTO> getUsersByGender(int page, int rows, String gender) {
        if (gender == null || gender.isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }

        char genderCode = Character.toUpperCase(gender.charAt(0));

        Page<UserSummaryProjection> users = userRepository.findAllByGender(Gender.getValidGenderFromCode(genderCode), PageRequest.of(page, rows));
        if (users.isEmpty()){
            return List.of();
        }
        return users.getContent()
                .stream()
                .map(UserSummaryDTO::new)
                .collect(Collectors.toList());
    }

    // check if emails is not used another user
    private void checkEmailIsNotUsed(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new ApplicationException(String.format("Email %s is already in use", email), ErrorType.EMAIL_DUPLICATED, null);
        }
    }
}
