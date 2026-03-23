package sk.mvp.user_service.auth.event;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sk.mvp.common.CookieUtils;
import sk.mvp.user_service.auth.dto.TokenPair;
import sk.mvp.user_service.auth.dto.UserDetail;
import sk.mvp.user_service.auth.service.ITokenService;
import sk.mvp.user_service.common.config.JwtConfig;
import sk.mvp.user_service.common.constants.AuthConts;
import sk.mvp.user_service.common.reddis.IRedisService;

import java.io.IOException;
import java.time.Duration;

@Component
public class LoginSuccesHandler implements AuthenticationSuccessHandler {
    private IRedisService redisService;
    private ITokenService tokenService;
    private JwtConfig jwtConfig;

    public LoginSuccesHandler(IRedisService redisService, ITokenService tokenService, JwtConfig jwtConfig) {
        this.redisService = redisService;
        this.tokenService = tokenService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String loginAttemptsKey = AuthConts.REDISS_AUTH_LOGIN_ATTEMPTS_USER_COLL + userDetail.getUsername();
        // delete attemts counter in reddis
        redisService.delete(loginAttemptsKey);
        //generate token pair
        TokenPair tokenPair = tokenService.generateTokenPair(userDetail);
        // send token to cookie
        //place refresh token in httopnly cookie
        ResponseCookie refreshCookie = CookieUtils.create("refresh_token",
                tokenPair.getRefreshToken(),
                jwtConfig.getCookieDomain(),
                Duration.ofMillis(jwtConfig.getRefreshTokenExpiration()),
                jwtConfig.isCookieIsHttpOnly(),
                jwtConfig.isCookieIsSecure(),
                jwtConfig.getRefreshTokenCookiePath(),
                jwtConfig.getCoikieSameSite()
        );
        //place acces token in http-only cookie
        ResponseCookie accessCookie = CookieUtils.create("access_token",
                tokenPair.getAccessToken(),
                jwtConfig.getCookieDomain(),
                Duration.ofMillis(jwtConfig.getAccesTokenExpiration()),
                jwtConfig.isCookieIsHttpOnly(),
                jwtConfig.isCookieIsSecure(),
                "/",
                jwtConfig.getCoikieSameSite()
        );
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);


    }
}
