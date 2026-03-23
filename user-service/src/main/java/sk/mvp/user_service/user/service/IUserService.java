package sk.mvp.user_service.user.service;

import sk.mvp.user_service.user.dto.UserProfile;
import sk.mvp.user_service.auth.dto.RegistrationReq;
import sk.mvp.user_service.admin.dto.UserSummary;

import java.util.List;

public interface IUserService {
    UserProfile getUserByFirstName(String firstName);
    UserProfile getUserByEmail(String email);
    UserProfile getUserByUsername(String username);
    void updateUserProfile(String userName, UserProfile userProfileDTO);
}
