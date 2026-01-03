package sk.mvp.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.dto.user.*;
import sk.mvp.user_service.service.IUserService;

import java.util.List;

@RestController
@RequestMapping(value = "api/users")
public class UserController {
    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
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
    public UserLoginRespDTO login(@RequestBody @Valid UserLoginReqDTO userLoginReqDTO, HttpServletRequest request) {
       return userService.loginUser(userLoginReqDTO, request);
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
