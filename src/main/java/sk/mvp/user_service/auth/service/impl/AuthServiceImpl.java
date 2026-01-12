package sk.mvp.user_service.auth.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.auth.dto.RegistrationReq;
import sk.mvp.user_service.auth.service.IAuthService;
import sk.mvp.user_service.auth.service.ITokenService;
import sk.mvp.user_service.auth.dto.TokenPair;
import sk.mvp.user_service.auth.dto.LoginReq;
import sk.mvp.user_service.common.exception.ApplicationException;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.entity.Contact;
import sk.mvp.user_service.entity.Gender;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.user.dto.UserProfile;
import sk.mvp.user_service.user.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {
    private ITokenService jwtService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;

    public AuthServiceImpl(ITokenService jwtService,
                           AuthenticationManager authenticationManager,
                           UserRepository userRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    public TokenPair loginUser(LoginReq loginReq) {
        //prebhene autetifikacia najdenie usera, provnanei hesla
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReq.username(),
                        loginReq.password()
                )
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return jwtService.generateTokenPair(userDetails);
    }

    @Override
    public TokenPair refreshTokens(String refreshToken) {
        return jwtService.refreshTokens(refreshToken);
    }

    @Override
    @Transactional
    public UserProfile registerUser(RegistrationReq registrationReq) {
        if (registrationReq == null) {
            throw new IllegalArgumentException("Registration user data cannot be null");
        }
        //TODO: 2.check if it is email in good pattern using validator, regex ?? Bean validator
        //1. check if email already exists
        checkEmailIsNotUsed(registrationReq.getEmail());

        // create new instance of user
        Contact contact = new Contact(registrationReq.getEmail());
        User user = new User(registrationReq.getUsername(),
                registrationReq.getPassword(),
                contact,
                Gender.getValidGenderFromCode(registrationReq.getGenderCodeAsCharacter()));
        //set user to new contact
        contact.setUser(user);
        // save user to DB
        User savedUser = userRepository.save(user);

        return new UserProfile(savedUser);
    }

    // check if emails is not used another user
    private void checkEmailIsNotUsed(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new ApplicationException(String.format("Email %s is already in use", email), ErrorType.EMAIL_DUPLICATED, null);
        }
    }
}
