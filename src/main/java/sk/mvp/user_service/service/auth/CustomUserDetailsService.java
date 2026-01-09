package sk.mvp.user_service.service.auth;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.dto.ErrorType;
import sk.mvp.user_service.dto.auth.UserDetail;
import sk.mvp.user_service.exception.ApplicationException;
import sk.mvp.user_service.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, ApplicationException {
        sk.mvp.user_service.model.User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ApplicationException("User with username " + username + " not found", ErrorType.USER_NOT_FOUND, null));
        return new UserDetail(user);
    }
}
