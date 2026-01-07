package sk.mvp.user_service.service.jwt;

import io.jsonwebtoken.JwtException;
import org.springframework.security.core.userdetails.UserDetails;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.exception.InvalidTokenException;

public interface ITokenService {
    /**
     * method to generate new pair of tokens
     * save refresh token to whitelist
     * @return access and refresh token
     */
    TokenPair generateTokenPair(String username);
    TokenPair refreshTokens(String refreshToken);

    /**
     * validate acces token and get user deatils from it
     * @param accessToken
     * @return
     * @throws InvalidTokenException
     */
    UserDetails getUserDetailFromAccessToken(String accessToken) throws JwtException;

    void validateAccessToken(String accessToken, UserDetails userDetails) throws InvalidTokenException;

    /**
     * add acces token to redis blacklist
     * @param accessToken
     */
    void revokeAccessToken(String accessToken);

    /**
     * delete refresh token from whitelist
     * @param refreshToken
     */
    void revokeRefreshToken(String refreshToken);
    //TODO: spravit vlastnu implemetaciu metody getTokenVersion , bude to cachovane aj v reddise pre rychlsot
    int getTokenVersion(String userName);


}
