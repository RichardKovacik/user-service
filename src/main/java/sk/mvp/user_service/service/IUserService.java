package sk.mvp.user_service.service;

import jakarta.servlet.http.HttpServletRequest;
import sk.mvp.user_service.dto.user.UserCreateDTO;
import sk.mvp.user_service.dto.user.UserLoginDTO;
import sk.mvp.user_service.dto.user.UserProfileDTO;
import sk.mvp.user_service.dto.user.UserSummaryDTO;

import java.util.List;

public interface IUserService {
    UserProfileDTO getUserByFirstName(String firstName);
    UserProfileDTO getUserByEmail(String email);
    UserProfileDTO getUserByUsername(String username);
    List<UserSummaryDTO> getUsers(int page, int rows);
    UserProfileDTO saveUser(UserCreateDTO user);
    void deleteUserbyUsername(String userName);
    void deleteUserbyEmailOptimized(String email);
    void assignRoleToUser(String username, String roleName);
    void unassignRoleFromUser(String username, String roleName);
    void updateUserProfile(String userName, UserProfileDTO userProfileDTO);
    List<UserSummaryDTO> getUsersByGender(int page, int rows, String gender);
    void loginUser(UserLoginDTO userLoginDTO, HttpServletRequest request);
}
