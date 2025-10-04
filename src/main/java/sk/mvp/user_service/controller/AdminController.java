package sk.mvp.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.service.IUserService;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private IUserService userService;

    public AdminController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/assignRole")
    public ResponseEntity<?> assignRole(@RequestParam String username, @RequestParam String roleName) {
        userService.assignRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/removeRole")
    public ResponseEntity<?>  removeRole(@RequestParam String username, @RequestParam String roleName) {
        userService.unassignRoleFromUser(username, roleName);
        return ResponseEntity.ok().build();
    }
}
