package sk.mvp.user_service.admin.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.admin.dto.UserStatusUpdateReq;
import sk.mvp.user_service.admin.dto.UserSummary;
import sk.mvp.user_service.admin.service.IAdminService;

import java.util.List;

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

    /*
     set status of user BLOCKCKED, UNBLOCKED
     */
    @PatchMapping(value = "/users/{userId}/status")
    public ResponseEntity<?> setUserStatus(@PathVariable(name = "userId") Long userId,
                                            @RequestBody @Valid UserStatusUpdateReq userStatusUpdateReq) {
        adminService.setUserStatus(userId, userStatusUpdateReq);
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
        //
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
