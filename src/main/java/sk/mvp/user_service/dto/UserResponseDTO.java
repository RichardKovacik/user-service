package sk.mvp.user_service.dto;

import sk.mvp.user_service.model.User;

import java.io.Serializable;

public class UserResponseDTO implements Serializable {
    private String name;
    private String lastName;

    public UserResponseDTO(User user) {
        this.name = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }
}
