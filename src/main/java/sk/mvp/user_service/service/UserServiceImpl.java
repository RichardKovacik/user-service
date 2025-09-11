package sk.mvp.user_service.service;

import org.springframework.stereotype.Service;
import sk.mvp.user_service.dto.UserResponseDTO;
import sk.mvp.user_service.exception.UserNotFoundException;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO getUserByFirstName(String firstName) {
        User user = userRepository.findByName(firstName).orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserResponseDTO(user);
    }
}
