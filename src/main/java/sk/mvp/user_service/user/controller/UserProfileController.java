package sk.mvp.user_service.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.common.config.JwtConfig;
import sk.mvp.user_service.user.dto.UserProfile;
import sk.mvp.user_service.auth.dto.RegistrationReq;
import sk.mvp.user_service.user.service.IUserService;
import sk.mvp.user_service.common.utils.JwtUtil;

@RestController
@RequestMapping(value = "api/profile")
public class UserProfileController {
    private IUserService userService;
    private JwtConfig jwtConfig;
    private JwtUtil jwtUtil;

    public UserProfileController(IUserService userService, JwtConfig jwtConfig,
                                 JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(value = "/get")
    public UserProfile getUserProfile(@NotNull @CookieValue(name = "access_token") String accesToken) {
        String username = jwtUtil.parseClaimsFromJwtToken(accesToken, jwtConfig.getAccesKey()).getSubject();
        return userService.getUserByUsername(username);
    }

//    @GetMapping(value = "/by-email/{email}")
//    public UserProfile getUserProfileByEmail(@PathVariable String email) {
//        return userService.getUserByEmail(email);
//    }

    @PatchMapping(value = "/update")
    public ResponseEntity<?> updateUserProfileData(@NotNull @CookieValue(name = "access_token") String accesToken,
                                                  @RequestBody @Valid UserProfile userProfileDTO) {
        String username = jwtUtil.parseClaimsFromJwtToken(accesToken, jwtConfig.getAccesKey()).getSubject();
        userService.updateUserProfile(username, userProfileDTO);
        return ResponseEntity.ok().build();
    }

}
