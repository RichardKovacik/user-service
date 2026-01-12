package sk.mvp.user_service.user.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.common.exception.ApplicationException;
import sk.mvp.user_service.entity.Contact;
import sk.mvp.user_service.entity.Gender;
import sk.mvp.user_service.entity.Role;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.projections.UserSummaryProjection;
import sk.mvp.user_service.user.dto.UserProfile;
import sk.mvp.user_service.auth.dto.RegistrationReq;
import sk.mvp.user_service.admin.dto.UserSummary;
import sk.mvp.user_service.user.repository.RoleRepository;
import sk.mvp.user_service.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public UserProfile getUserByFirstName(String firstName) {
        User user = userRepository.findByFirstName(firstName).orElseThrow(() -> new ApplicationException(
                String.format("User with firstName %s not found",
                firstName),
                ErrorType.USER_NOT_FOUND, null));
        return new UserProfile(user);
    }

    @Override
    @Transactional
    public UserProfile getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ApplicationException("User with email " + email + " not found", ErrorType.USER_NOT_FOUND, null));
        return new UserProfile(user);
    }

    @Override
    @Transactional
    public UserProfile getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApplicationException("User with username " + username + " not found", ErrorType.USER_NOT_FOUND, null));
        return new UserProfile(user);
    }

    @Transactional
    @Override
    public void updateUserProfile(String userName, UserProfile userProfileDTO) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        User user = userRepository.findByUsername(userName).orElseThrow(() ->
                new ApplicationException("User with username " + userName + " not found", ErrorType.USER_NOT_FOUND, null));

        if (userProfileDTO.getFirstName() != null && !userProfileDTO.getFirstName().isEmpty()) {
            user.setFirstName(userProfileDTO.getFirstName());
        }
        if (userProfileDTO.getLastName() != null && !userProfileDTO.getLastName().isEmpty()) {
            user.setLastName(userProfileDTO.getLastName());
        }

        userRepository.save(user);

    }

}
