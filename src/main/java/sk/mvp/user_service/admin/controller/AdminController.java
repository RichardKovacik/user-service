package sk.mvp.user_service.admin.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.admin.dto.UserEnabledReq;
import sk.mvp.user_service.admin.dto.UserSummary;
import sk.mvp.user_service.admin.service.IAdminService;
import sk.mvp.user_service.user.dto.UserProfile;
import sk.mvp.user_service.user.service.IUserService;
import sk.mvp.user_service.auth.service.IAuthService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }
    @DeleteMapping(value = "/revokeTokens")
    public ResponseEntity<?> removeRole(@RequestParam String username) {
        adminService.revokeTokens(username);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/user/enable")
    public ResponseEntity<?> setUserEnabled(@RequestParam String username,
                                            @RequestBody @Valid UserEnabledReq userEnabledReq) {
        adminService.setUserEnabled(username, userEnabledReq.enabled());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/assignRole")
    public ResponseEntity<?> assignRole(@RequestParam String username, @RequestParam String roleName) {
        adminService.assignRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }

    // excplicitly logout user in all sessions, ivalidate all sessions
    @DeleteMapping(value = "/removeRole")
    public ResponseEntity<?> removeRole(@RequestParam String username, @RequestParam String roleName) {
        adminService.unassignRoleFromUser(username, roleName);
        return ResponseEntity.ok().build();
    }
    @GetMapping(value = "/user/list")
    public List<UserSummary> getUsers(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size) {
        return adminService.getUsers(page, size);
    }

    @GetMapping(value = "/user/filter/by-gender")
    public List<UserSummary> getUsersByGender(@RequestParam String genderCode,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size) {
        return adminService.getUsersByGender(page, size, genderCode);
    }
    @DeleteMapping(value = "/user/delete/by-username/{username}")
    public ResponseEntity<?> deleteUserByUserName(@PathVariable String username) {
        adminService.deleteUserbyUsername(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/user/delete/by-email/{username}")
    public ResponseEntity<?> deleteUserByEmailOptimized(@PathVariable String username) {
        adminService.deleteUserbyEmailOptimized(username);
        return ResponseEntity.ok().build();
    }

}
