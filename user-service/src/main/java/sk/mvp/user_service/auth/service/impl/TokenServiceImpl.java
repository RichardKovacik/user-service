package sk.mvp.user_service.auth.service.impl;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.auth.service.ITokenService;
import sk.mvp.user_service.common.config.JwtConfig;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.auth.dto.UserDetail;
import sk.mvp.user_service.auth.dto.TokenPair;
import sk.mvp.user_service.common.exception.QApplicationException;
import sk.mvp.user_service.common.exception.InvalidTokenException;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.user.repository.UserRepository;
import sk.mvp.user_service.common.reddis.IRedisService;
import sk.mvp.user_service.common.utils.JwtUtil;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements ITokenService {
    private IRedisService redisService;
    private JwtConfig jwtConfig;
    private UserRepository userRepository;

    public TokenServiceImpl(IRedisService redisService,
                            JwtConfig jwtConfig,
                            UserRepository userRepository) {
        this.redisService = redisService;
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
    }

    @Override
    public TokenPair generateTokenPair(UserDetails userDetails) {
        int tokenVersion = getTokenVersion(userDetails.getUsername());
        String jtiRefresh = UUID.randomUUID().toString();
        String jtiAccess = UUID.randomUUID().toString();

        String accessToken = JwtUtil.generateAccessToken(userDetails.getUsername(),
                tokenVersion,
                jtiAccess,
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new),
                jwtConfig.getAccesKey(),
                jwtConfig.getAccesTokenExpiration());
        String refreshToken = JwtUtil.generateRefreshToken(jtiRefresh, jwtConfig.getRefreshKey(), jwtConfig.getRefreshTokenExpiration());
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
        //check if refresh token is valid, signing, expiratuon, type, fields...
        JwtUtil.validateRefreshToken(refreshToken, jwtConfig.getRefreshKey());
        //parse claims from token
        Claims claims = JwtUtil.parseClaimsFromJwtToken(refreshToken, jwtConfig.getRefreshKey());
        //check if refresh is in whitelist
        String refreshKey = "auth:refresh:token:"+claims.getId();
        String userName = String.valueOf(redisService.get(refreshKey)
                .orElseThrow(()-> new QApplicationException(
                        "Refresh token reuse detected",
                        ErrorType.TOKEN_REUSED_DETECTED,
                        null)));
        //delete actual refresh token from whitelist
        revokeRefreshToken(refreshToken);
        //delete refresh token from Set reddis (cron job bude mazat na tzydnnej baze tokeny zo setu)

        //get userdetai from db
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new QApplicationException("User with username " + userName + " not found", ErrorType.USER_NOT_FOUND, null));

        //generate new pairs and also add in reddis whotelist noew refresh token
        return generateTokenPair(new UserDetail(user));
    }

    @Override
    public UserDetail getUserDetailFromAccessToken(String accessToken) throws InvalidTokenException {
       return JwtUtil.getUserDetailFromAccessToken(accessToken, jwtConfig.getAccesKey());
    }

    @Override
    public void validateAccessToken(String accessToken, UserDetails userDetails) throws InvalidTokenException {
        try {
            int tokenVersion = getTokenVersion(userDetails.getUsername());
            JwtUtil.validateAccessToken(accessToken, tokenVersion, jwtConfig.getAccesKey());
            // validate if accesa token is in blacklist
            Claims claims = JwtUtil.parseClaimsFromJwtToken(accessToken, jwtConfig.getAccesKey());
            String key = "auth:access:blacklist:" + claims.getId();
            if (redisService.has(key)) {
                throw new QApplicationException("Access token is in blacklist. Possible security breach !!", ErrorType.TOKEN_REUSED_DETECTED, null);
            }

        }catch (Exception e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }

    @Override
    public void revokeAccessToken(String accessToken) {
        //add access token to blacklist for reaming time to live of token, used when logout
        Claims claims = JwtUtil.parseClaimsFromJwtToken(accessToken, jwtConfig.getAccesKey());
        String key = "auth:access:blacklist:" + claims.getId();
        redisService.set(key, claims.getId(), JwtUtil.ttlUntilExpiration(claims));
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        String tokenId = JwtUtil.parseClaimsFromJwtToken(refreshToken, jwtConfig.getRefreshKey()).getId();
        String key = "auth:refresh:token:" + tokenId;
        redisService.delete(key);
    }


    @Override
    public int getTokenVersion(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }

        String key = "auth:access:user:tokenVersion:" + userName;

        // try hit redis chache
        Optional<String> cachedValue = redisService.get(key).map(Object::toString);
        if (cachedValue.isPresent()) {
            return Integer.parseInt(cachedValue.get());
        }
        // cache is empty, try hit real db
        int tokenVersion = userRepository.getTokenVersion(userName)
                .orElseThrow(() -> new QApplicationException("User with username " + userName + " not found", ErrorType.USER_NOT_FOUND, null));
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
        Set<String> refreshJtis = redisService.getSet(userRefreshTokensKey).stream().map(Object::toString).collect(Collectors.toSet());

        //delete refresh token from whitelist
        for(String key : refreshJtis) {
            redisService.delete("auth:refresh:token:" + key);
        }

        //delete all refresh tokens from set
        redisService.delete(userRefreshTokensKey);

        // delete cached reddis  auth:user:tokenVersion
        String tokenVersionKey = "auth:access:user:tokenVersion:" + userName;
        redisService.delete(tokenVersionKey);
    }
}
