package sk.mvp.user_service.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.dto.ErrorType;
import sk.mvp.user_service.dto.user.UserProfileDTO;
import sk.mvp.user_service.exception.ApplicationException;
import sk.mvp.user_service.repository.UserRepository;
import sk.mvp.user_service.service.IUserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, ApplicationException {
        sk.mvp.user_service.model.User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApplicationException("User with username " + username + " not found", ErrorType.USER_NOT_FOUND, null));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRolesAsString())
                .build();
    }
}
