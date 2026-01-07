package sk.mvp.user_service.service.jwt;

import io.jsonwebtoken.JwtException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.config.JwtConfig;
import sk.mvp.user_service.dto.ErrorType;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.exception.ApplicationException;
import sk.mvp.user_service.exception.InvalidTokenException;
import sk.mvp.user_service.repository.UserRepository;
import sk.mvp.user_service.security.CustomUserDetailsService;
import sk.mvp.user_service.service.redis.IRedisService;
import sk.mvp.user_service.util.JwtUtil;

import java.time.Duration;
import java.util.UUID;

@Service
public class TokenServiceImpl implements ITokenService {
    private IRedisService redisService;
    private JwtUtil jwtUtil;
    private CustomUserDetailsService customUserDetailsService;
    private JwtConfig jwtConfig;
    private UserRepository userRepository;

    public TokenServiceImpl(IRedisService redisService, JwtUtil jwtUtil,
                            CustomUserDetailsService customUserDetailsService,
                            JwtConfig jwtConfig, UserRepository userRepository) {
        this.redisService = redisService;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
    }

    @Override
    public TokenPair generateTokenPair(String username) {
        int tokenVersion = getTokenVersion(username);
        String jti = UUID.randomUUID().toString();

        String accessToken = this.jwtUtil.generateAccessToken(username, tokenVersion);
        String refreshToken = this.jwtUtil.generateRefreshToken(jti);

        redisService.set("auth:refresh:token:"+ jti, username,
                Duration.ofMillis(jwtConfig.getAccesTokenExpiration()));

        return new TokenPair(refreshToken, accessToken);
    }

    @Override
    public TokenPair refreshTokens(String refreshToken) {
        return null;
    }

    @Override
    public UserDetails getUserDetailFromAccessToken(String accessToken) {
        UserDetails userDetails = null;
        try {
            String username = jwtUtil.getUsernameFromAccessToken(accessToken);
            userDetails = customUserDetailsService.loadUserByUsername(username);
        }catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
        return userDetails;
    }

    @Override
    public void validateAccessToken(String accessToken, UserDetails userDetails) throws InvalidTokenException {
        try {
            int tokenVersion = getTokenVersion(userDetails.getUsername());
            jwtUtil.validateAccessToken(accessToken, tokenVersion);
        }catch (Exception e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }

    @Override
    public void revokeAccessToken(String accessToken) {

    }

    @Override
    public void revokeRefreshToken(String refreshToken) {

    }


    @Override
    public int getTokenVersion(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        return userRepository.getTokenVersion(userName)
                .orElseThrow(() -> new ApplicationException("User with username " + userName + " not found", ErrorType.USER_NOT_FOUND, null));
    }
}
