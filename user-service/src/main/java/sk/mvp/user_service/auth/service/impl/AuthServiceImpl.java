package sk.mvp.user_service.auth.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.auth.dto.RegistrationReq;
import sk.mvp.user_service.auth.dto.VerificationTokenResponse;
import sk.mvp.user_service.auth.service.IAuthService;
import sk.mvp.user_service.auth.service.ITokenService;
import sk.mvp.user_service.auth.dto.TokenPair;
import sk.mvp.user_service.auth.dto.LoginReq;
import sk.mvp.user_service.auth.service.IVerificationTokenService;
import sk.mvp.user_service.common.constants.AuthConts;
import sk.mvp.user_service.common.exception.QApplicationException;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.common.reddis.IRedisService;
import sk.mvp.user_service.entity.Contact;
import sk.mvp.user_service.entity.Gender;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.entity.VerificationToken;
import sk.mvp.user_service.user.dto.UserProfile;
import sk.mvp.user_service.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {
    private ITokenService jwtService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private IRedisService redisService;
    private IVerificationTokenService verificationTokenService;

    public AuthServiceImpl(ITokenService jwtService,
                           AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           IRedisService redisService,
                           IVerificationTokenService verificationTokenService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.verificationTokenService = verificationTokenService;
    }

    @Override
    public TokenPair loginUser(LoginReq loginReq) {
        String loginAttemptsKey = AuthConts.REDISS_AUTH_LOGIN_ATTEMPTS_USER_COLL + loginReq.username();
        String lockedUserKey = AuthConts.REDISS_AUTH_BLACKLIST_USER_COLL + loginReq.username();

        // result = -1 user is locked(reach max attemts of login)
        Long result = redisService.executeLuaScript("redis/login_attempts.lua",
                List.of(loginAttemptsKey,lockedUserKey),
                300, AuthConts.MAX_LOGIN_ATTEMPTS,60);

        //if user is locked throw exception
        if (result == -1) {
            throw new QApplicationException("You have tried too many times, please try again later.",
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
            throw new QApplicationException(e.getMessage(), ErrorType.AUTH_INVALID_CREDENTIALS, null);
//            Optional<String> value = redisService.get(loginAttemptsKey);
//            if (value.isEmpty()) {
//                //setup reddis collection with 5 min TTL
//                redisService.set(loginAttemptsKey,"1", Duration.ofSeconds(300));
//            } else {
//                if (Integer.parseInt(value.get()) + 1 >= AuthConts.MAX_LOGIN_ATTEMPTS) {
//                    // lock user to specific time, add to blacklist
//                    redisService.set(lockedUserKey,"locked", Duration.ofSeconds(60));
//                    // delete loginAttempts collection
//                    redisService.delete(loginAttemptsKey);
//                    // throw excpetion too many req
//                    throw new ApplicationException("You have tried too many times, please try again later.",
//                            ErrorType.TOO_MANY_REQUESTS,
//                            null);
//                }else {
//                    redisService.increment(loginAttemptsKey);
//                }
//
//            }
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
        // save verificationToken to DB
        this.verificationTokenService.createVerificationToken(savedUser);
        return new UserProfile(savedUser);
    }

    @Override
    public void logout(String refreshToken, String accessToken) {
        // remove refresh token from reddis
        jwtService.revokeRefreshToken(refreshToken);
        // add acces token to blacklist
        jwtService.revokeAccessToken(accessToken);
    }

    @Transactional
    @Override
    public VerificationTokenResponse verifyEmailVerificationToken(String verificationToken) {
        VerificationToken foundedToken = verificationTokenService.getVerificationToken(verificationToken);

        User user = foundedToken.getUser();
        if (user.isEmailVerified()) {
            return new VerificationTokenResponse("Email is already verified");
        }

        if (foundedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new QApplicationException(ErrorType.VERIFICATION_TOKEN_EXPIRED);
        }

        if (foundedToken.isUsed()) {
            throw new QApplicationException(ErrorType.VERIFICATION_TOKEN_INVALID);

        }
        // hibernate has persistance context, and in the end save updted obejects to db
        foundedToken.setUsed(true);
        user.setEmailVerified(true);

        return new VerificationTokenResponse("Email successfully verified");
    }


    // check if emails is not used another user
    private void checkEmailIsNotUsed(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new QApplicationException(String.format("Email %s is already in use", email), ErrorType.EMAIL_DUPLICATED, null);
        }
    }
}
