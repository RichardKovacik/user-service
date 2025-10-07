package sk.mvp.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.dto.user.UserCreateDTO;
import sk.mvp.user_service.dto.user.UserProfileDTO;
import sk.mvp.user_service.dto.user.UserSummaryDTO;
import sk.mvp.user_service.service.IUserService;

import java.util.List;

@RestController
@RequestMapping(value = "api/users")
public class UserController {
    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
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

}
