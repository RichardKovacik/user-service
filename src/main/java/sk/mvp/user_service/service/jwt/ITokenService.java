package sk.mvp.user_service.service.jwt;

import io.jsonwebtoken.JwtException;
import org.springframework.security.core.userdetails.UserDetails;
import sk.mvp.user_service.dto.auth.UserDetail;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.exception.InvalidTokenException;

public interface ITokenService {
    /**
     * method to generate new pair of tokens from user details data
     * save refresh token to reddis
     * @return access and refresh token
     */
    TokenPair generateTokenPair(UserDetails userDetails);
    TokenPair refreshTokens(String refreshToken);

    /**
     * validate acces token and get user deatils from it
     * @param accessToken
     * @return
     * @throws InvalidTokenException
     */
    UserDetail getUserDetailFromAccessToken(String accessToken) throws JwtException, InvalidTokenException;

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

    /**
     * Cached opetaion to get token version from db
     * 1st call hits db and then others hit cached token version stored in reddis
     * @param userName
     * @return token version as integer
     */
    int getTokenVersion(String userName);

    /**
     * method to revoke all tokens of user in case of security incident
     * only Admins are allowed to use it, it foces logout in any user device
     * @param userName
     */
    void revokeAllTokens(String userName);


}
