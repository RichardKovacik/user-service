package sk.mvp.user_service.auth.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.auth.dto.UserDetail;
import sk.mvp.user_service.common.exception.ApplicationException;
import sk.mvp.user_service.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        sk.mvp.user_service.entity.User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username not found"));
        return new UserDetail(user);
    }
}
