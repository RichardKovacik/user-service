package sk.mvp.user_service.auth.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.auth.dto.RegistrationReq;
import sk.mvp.user_service.auth.service.IAuthService;
import sk.mvp.user_service.auth.service.ITokenService;
import sk.mvp.user_service.auth.dto.TokenPair;
import sk.mvp.user_service.auth.dto.LoginReq;
import sk.mvp.user_service.common.constants.AuthConts;
import sk.mvp.user_service.common.exception.ApplicationException;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.common.reddis.IRedisService;
import sk.mvp.user_service.entity.Contact;
import sk.mvp.user_service.entity.Gender;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.user.dto.UserProfile;
import sk.mvp.user_service.user.repository.UserRepository;

import java.time.Duration;
import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {
    private ITokenService jwtService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private IRedisService redisService;

    public AuthServiceImpl(ITokenService jwtService,
                           AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           IRedisService redisService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.redisService = redisService;
    }

    @Override
    public TokenPair loginUser(LoginReq loginReq) {
        String loginAttemptsKey = AuthConts.REDISS_AUTH_LOGIN_ATTEMPTS_USER_COLL + loginReq.username();
        String lockedUserKey = AuthConts.REDISS_AUTH_BLACKLIST_USER_COLL + loginReq.username();
        //check if user is not locked
        if (redisService.has(lockedUserKey)) {
            throw new ApplicationException("You have tried too many times, please try again later.",
                    ErrorType.TOO_MANY_REQUESTS,
                    null);
        }

        // prebhene autetifikacia najdenie usera, provnanei hesla -> customUserDetailService
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginReq.username(),
                            loginReq.password()
                    )
            );
        }catch (BadCredentialsException | UsernameNotFoundException e) {
            Optional<String> value = redisService.get(loginAttemptsKey);
            if (value.isEmpty()) {
                //setup reddis collection with 5 min TTL
                redisService.set(loginAttemptsKey,"1", Duration.ofSeconds(300));
            } else {
                if (Integer.parseInt(value.get()) + 1 >= AuthConts.MAX_LOGIN_ATTEMPTS) {
                    // lock user to specific time, add to blacklist
                    redisService.set(lockedUserKey,"locked", Duration.ofSeconds(60));
                    // delete loginAttempts collection
                    redisService.delete(loginAttemptsKey);
                    // throw excpetion too many req
                    throw new ApplicationException("You have tried too many times, please try again later.",
                            ErrorType.TOO_MANY_REQUESTS,
                            null);
                }else {
                    redisService.increment(loginAttemptsKey);
                }

            }
            throw new ApplicationException(e.getMessage(), ErrorType.INVALID_CREDENTIAL, null);
        }
        //success login, remove attempts counter collection in reddis if exists
        redisService.delete(loginAttemptsKey);

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

    @Override
    public void logout(String refreshToken, String accessToken) {
        // remove refresh token from reddis
        jwtService.revokeRefreshToken(refreshToken);
        // add acces token to blacklist
        jwtService.revokeAccessToken(accessToken);
    }

    // check if emails is not used another user
    private void checkEmailIsNotUsed(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new ApplicationException(String.format("Email %s is already in use", email), ErrorType.EMAIL_DUPLICATED, null);
        }
    }
}
