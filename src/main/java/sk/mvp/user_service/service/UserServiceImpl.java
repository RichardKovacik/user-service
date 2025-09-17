package sk.mvp.user_service.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.dto.UserResponseDTO;
import sk.mvp.user_service.exception.UserNotFoundException;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponseDTO getUserByFirstName(String firstName) {
        User user = userRepository.findByFirstName(firstName).orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
        return new UserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getUsers(int page, int rows) {
       Page<User> users = userRepository.findAll(PageRequest.of(page, rows));
       if (users.isEmpty()){
           return List.of();
       }
       return users.getContent()
               .stream()
               .map(UserResponseDTO::new)
               .collect(Collectors.toList());
    }
}
