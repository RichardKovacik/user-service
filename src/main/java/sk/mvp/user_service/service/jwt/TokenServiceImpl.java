package sk.mvp.user_service.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.config.JwtConfig;
import sk.mvp.user_service.dto.ErrorType;
import sk.mvp.user_service.dto.auth.UserDetail;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.exception.ApplicationException;
import sk.mvp.user_service.exception.InvalidTokenException;
import sk.mvp.user_service.repository.UserRepository;
import sk.mvp.user_service.service.auth.CustomUserDetailsService;
import sk.mvp.user_service.service.redis.IRedisService;
import sk.mvp.user_service.util.JwtUtil;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TokenServiceImpl implements ITokenService {
    private IRedisService redisService;
    private JwtUtil jwtUtil;
    private JwtConfig jwtConfig;
    private UserRepository userRepository;

    public TokenServiceImpl(IRedisService redisService, JwtUtil jwtUtil,
                            JwtConfig jwtConfig, UserRepository userRepository) {
        this.redisService = redisService;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
    }

    @Override
    public TokenPair generateTokenPair(UserDetails userDetails) {
        int tokenVersion = getTokenVersion(userDetails.getUsername());
        String jtiRefresh = UUID.randomUUID().toString();
        String jtiAccess = UUID.randomUUID().toString();

        String accessToken = this.jwtUtil.generateAccessToken(userDetails.getUsername(),
                tokenVersion,
                jtiAccess,
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new));
        String refreshToken = this.jwtUtil.generateRefreshToken(jtiRefresh);
        String userRefreshTokensKey = "auth:refresh:user:"+ userDetails.getUsername();

        //add refresh token to whitelist
        redisService.set("auth:refresh:token:"+ jtiRefresh, userDetails.getUsername(),
                Duration.ofMillis(jwtConfig.getAccesTokenExpiration()));

        // add refresh token to reddis userName: setTokens(jti,jti,jti)
        redisService.addValueToSet(userRefreshTokensKey, jtiRefresh);


        return new TokenPair(refreshToken, accessToken);
    }

    @Override
    public TokenPair refreshTokens(String refreshToken) {
        return null;
    }

    @Override
    public UserDetail getUserDetailFromAccessToken(String accessToken) throws InvalidTokenException {
       return jwtUtil.getUserDetailFromAccessToken(accessToken);
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
        //add access token to blacklist for reaming time to live of token, used when logout
        Claims claims = jwtUtil.parseClaimsFromJwtToken(accessToken, jwtConfig.getAccesKey());
        String key = "auth:access:blacklist:" + claims.getId();
        redisService.set(key, accessToken, jwtUtil.ttlUntilExpiration(claims));
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        String key = "auth:refresh:token:"+ jwtUtil.parseClaimsFromJwtToken(refreshToken, jwtConfig.getRefreshKey()).getId();
        redisService.delete(key);
    }


    @Override
    public int getTokenVersion(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }

        String key = "auth:user:tokenVersion:" + userName;

        // try hit redis chache
        Optional<String> cachedValue = redisService.get(key);
        if (cachedValue.isPresent()) {
            return Integer.parseInt(cachedValue.get());
        }
        // cache is empty, try hit real db
        int tokenVersion = userRepository.getTokenVersion(userName)
                .orElseThrow(() -> new ApplicationException("User with username " + userName + " not found", ErrorType.USER_NOT_FOUND, null));
        // save value to the redis cache
        redisService.set(key,
                String.valueOf(tokenVersion),
                Duration.ofMillis(jwtConfig.getAccesTokenExpiration()));

        return tokenVersion;
    }

    @Override
    @Transactional
    public void revokeAllTokens(String userName) {

        // increase token version for security concern
        userRepository.incrementTokenVersion(userName);

        //get all user refresh tokens from set
        String userRefreshTokensKey = "auth:refresh:user:" + userName;
        Set<String> refreshJtis = redisService.getSet(userRefreshTokensKey);

        //delete refresh token from whitelist
        for(String key : refreshJtis) {
            redisService.delete("auth:refresh:token:" + key);
        }

        //delete all refresh tokens from set
        redisService.delete(userRefreshTokensKey);

        // delete cached reddis  auth:user:tokenVersion
        String tokenVersionKey = "auth:user:tokenVersion:" + userName;
        redisService.delete(tokenVersionKey);
    }
}
