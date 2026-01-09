package sk.mvp.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.*;
import sk.mvp.user_service.service.IUserService;
import sk.mvp.user_service.service.auth.IAuthService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private IUserService userService;
    private final SessionRegistry sessionRegistry;
    private IAuthService authService;

    public AdminController(IUserService userService, SessionRegistry sessionRegistry, IAuthService authService) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
        this.authService = authService;
    }
    @DeleteMapping(value = "/revokeTokens")
    public ResponseEntity<?>  removeRole(@RequestParam String username) {
        authService.revokeTokens(username);
        return ResponseEntity.ok().build();
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
    @GetMapping("/sessions")
    public Map<String, Object> getActiveSessions() {

        List<Object> principals = sessionRegistry.getAllPrincipals();

        int activeUsers = 0;

        for (Object principal : principals) {
            List<SessionInformation> sessions =
                    sessionRegistry.getAllSessions(principal, false);

            if (!sessions.isEmpty()) {
                activeUsers++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("activeUsers", activeUsers);
        result.put("sessions", sessionRegistry.getAllSessions(principals, false).size());

        return result;
    }

}
