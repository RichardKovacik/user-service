package sk.mvp.user_service.admin.service;

import sk.mvp.user_service.admin.dto.UserSummary;

import java.util.List;

public interface IAdminService {
    void assignRoleToUser(String username, String roleName);
    void unassignRoleFromUser(String username, String roleName);
    void deleteUserbyUsername(String userName);
    void deleteUserbyEmailOptimized(String email);
    List<UserSummary> getUsers(int page, int rows);
    List<UserSummary> getUsersByGender(int page, int rows, String gender);
    void revokeTokens(String username);
    void setUserEnabled(String username, boolean isEnabled);
}
