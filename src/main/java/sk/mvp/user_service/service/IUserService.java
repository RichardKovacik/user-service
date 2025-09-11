package sk.mvp.user_service.service;

import sk.mvp.user_service.dto.UserResponseDTO;
import sk.mvp.user_service.model.User;

public interface IUserService {
    UserResponseDTO getUserByFirstName(String firstName);
}
