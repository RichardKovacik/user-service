package sk.mvp.user_service.service;

import sk.mvp.user_service.dto.UserRequestDTO;
import sk.mvp.user_service.dto.UserResponseDTO;

import java.util.List;

public interface IUserService {
    UserResponseDTO getUserByFirstName(String firstName);
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO getUserByUsername(String username);
    List<UserResponseDTO> getUsers(int page, int rows);
    UserResponseDTO saveUser(UserRequestDTO user);
    void deleteUserbyUsername(String userName);
    void assignRoleToUser(String username, String roleName);
    void unassignRoleFromUser(String username, String roleName);
}
