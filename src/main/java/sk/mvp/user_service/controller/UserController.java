package sk.mvp.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.config.JwtConfig;
import sk.mvp.user_service.dto.jwt.RefreshTokenReq;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.dto.user.*;
import sk.mvp.user_service.service.IUserService;
import sk.mvp.user_service.service.auth.IAuthService;
import sk.mvp.user_service.util.CookieUtils;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping(value = "api/users")
public class UserController {
    private IUserService userService;
    private IAuthService authService;
    private JwtConfig jwtConfig;

    public UserController(IUserService userService, IAuthService authService, JwtConfig jwtConfig) {
        this.userService = userService;
        this.authService = authService;
        this.jwtConfig = jwtConfig;
    }

    @GetMapping("/get/token")
    public ResponseEntity<?> getToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            System.out.println("CSRF Token: " + token.getToken());  // alebo log.debug
        }
        // tvoja logika
        assert token != null;
        return ResponseEntity.ok().body(token.getToken());
    }
    @PostMapping(value = "/login")
    public TokenPair login(@RequestBody @Valid UserLoginReqDTO userLoginReqDTO, HttpServletRequest request) {
       return authService.loginUser(userLoginReqDTO);
    }

    /**
     * Endpint calls from client web apps
     * using http-only cookie
     * @param userLoginReqDTO
     * @return tokens pair in cookie for browser
     */
    @PostMapping(value = "/web/login")
    public ResponseEntity<?> webLogin(@RequestBody @Valid UserLoginReqDTO userLoginReqDTO, HttpServletResponse response) {
        TokenPair tokenPair = authService.loginUser(userLoginReqDTO);
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

    @GetMapping(value = "/by-name/{firstName}")
    public UserProfileDTO getUserByFirstName(@PathVariable String firstName) {
        return userService.getUserByFirstName(firstName);
    }

    @GetMapping(value = "/by-email/{email}")
    public UserProfileDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @DeleteMapping(value = "delete/by-username/{username}")
    public ResponseEntity<?> deleteUserByUserName(@PathVariable String username) {
        userService.deleteUserbyUsername(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "delete/by-email/{username}")
    public ResponseEntity<?> deleteUserByEmailOptimized(@PathVariable String username) {
        userService.deleteUserbyEmailOptimized(username);
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/list")
    public List<UserSummaryDTO> getUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        return userService.getUsers(page, size);
    }

    @GetMapping(value = "/filter/by-gender")
    public List<UserSummaryDTO> getUsersByGender(@RequestParam String genderCode,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size) {
        return userService.getUsersByGender(page, size, genderCode);
    }

    @PostMapping(value = "/create")
    public UserProfileDTO createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        return userService.saveUser(userCreateDTO);
    }

    @PatchMapping(value = "/update/{username}")
    public ResponseEntity<?> updateUserProfileData(@PathVariable String username,
                                                  @RequestBody @Valid UserProfileDTO userProfileDTO) {
        userService.updateUserProfile(username, userProfileDTO);
        return ResponseEntity.ok().build();
    }

}
