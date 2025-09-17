package sk.mvp.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.dto.UserRequestDTO;
import sk.mvp.user_service.dto.UserResponseDTO;
import sk.mvp.user_service.service.IUserService;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/by-name/{firstName}")
    public UserResponseDTO getUserByFirstName(@PathVariable String firstName) {
        return userService.getUserByFirstName(firstName);
    }

    @GetMapping(value = "/by-email/{email}")
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping(value = "/list")
    public List<UserResponseDTO> getUsers(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size) {
        return userService.getUsers(page, size);
    }

    @PostMapping(value = "/create")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.saveUser(userRequestDTO);
    }

}
