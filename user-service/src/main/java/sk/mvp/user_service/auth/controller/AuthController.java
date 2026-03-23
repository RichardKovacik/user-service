package sk.mvp.user_service.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.mvp.common.CookieUtils;
import sk.mvp.user_service.auth.dto.VerificationTokenResponse;
import sk.mvp.user_service.auth.dto.LoginReq;
import sk.mvp.user_service.auth.dto.RefreshTokenReq;
import sk.mvp.user_service.auth.dto.RegistrationReq;
import sk.mvp.user_service.auth.dto.TokenPair;
import sk.mvp.user_service.auth.service.IAuthService;
import sk.mvp.user_service.common.config.JwtConfig;
import sk.mvp.user_service.user.dto.UserProfile;

import java.time.Duration;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private IAuthService authService;
    private JwtConfig jwtConfig;

    public AuthController(IAuthService authService, JwtConfig jwtConfig) {
        this.authService = authService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping(value = "/login")
    public TokenPair login(@RequestBody @Valid LoginReq loginReq, HttpServletRequest request) {
        return authService.loginUser(loginReq);
    }

//    /**
//     * Endpint calls from client web apps
//     * using http-only cookie
//     * @param loginReq
//     * @return tokens pair in cookie for browser
//     */
//    @PostMapping(value = "/web/login")
//    public ResponseEntity<?> webLogin(@RequestBody @Valid LoginReq loginReq, HttpServletResponse response) {
       // TokenPair tokenPair = authService.loginUser(loginReq);
        //place refresh token in httopnly cookie
//        ResponseCookie refreshCookie = CookieUtils.create("refresh_token",
//                tokenPair.getRefreshToken(),
//                jwtConfig.getCookieDomain(),
//                Duration.ofMillis(jwtConfig.getRefreshTokenExpiration()),
//                jwtConfig.isCookieIsHttpOnly(),
//                jwtConfig.isCookieIsSecure(),
//                jwtConfig.getRefreshTokenCookiePath(),
//                jwtConfig.getCoikieSameSite()
//        );
//        //place acces token in http-only cookie
//        ResponseCookie accessCookie = CookieUtils.create("access_token",
//                tokenPair.getAccessToken(),
//                jwtConfig.getCookieDomain(),
//                Duration.ofMillis(jwtConfig.getAccesTokenExpiration()),
//                jwtConfig.isCookieIsHttpOnly(),
//                jwtConfig.isCookieIsSecure(),
//                "/",
//                jwtConfig.getCoikieSameSite()
//        );
//        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
//        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

//        return ResponseEntity.ok().build();
//    }

    @PostMapping(value = "/refresh/tokens")
    public TokenPair refreshTokens(@RequestBody @Valid RefreshTokenReq refreshTokenReq) {
        return authService.refreshTokens(refreshTokenReq.getToken());
    }

    /**
     * Refresh token from cookie
     * @return
     */
    @PostMapping(value = "/web/refresh/tokens")
    public ResponseEntity<?> refreshTokensWeb(@NotNull @CookieValue(name = "refresh_token") String refreshToken,
                                              HttpServletResponse response) {

        TokenPair tokenPair = authService.refreshTokens(refreshToken);
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
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/registration")
    public UserProfile createUser(@RequestBody @Valid RegistrationReq registrationReq) {
        return authService.registerUser(registrationReq);
    }

    @GetMapping(value = "/email/verify")
    public VerificationTokenResponse verifyToken(@RequestParam("token") @NotNull @NotBlank String token) {
        return authService.verifyEmailVerificationToken(token);
    }



    @PostMapping(value = "/web/logout")
    public ResponseEntity<?> logoutUser(@NotNull @CookieValue(name = "refresh_token") String refreshToken,
                                  @NotNull @CookieValue(name = "access_token") String accessToken,
                                  HttpServletResponse response) {
        authService.logout(refreshToken, accessToken);
        // clear tokens in cookie
        ResponseCookie refreshCookie = CookieUtils.removed("refresh_token", jwtConfig.getCookieDomain(), true);
        ResponseCookie accessCookie = CookieUtils.removed("access_token", jwtConfig.getCookieDomain(), true);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        return ResponseEntity.ok().build();
    }
}
