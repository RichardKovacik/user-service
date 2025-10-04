package sk.mvp.user_service.dto;

import sk.mvp.user_service.model.User;

import java.io.Serializable;

public class UserResponseDTO implements Serializable {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private char genderCode;

    public UserResponseDTO(User user) {
        if (user != null) {
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.genderCode = user.getGender().getCode();
            this.email = null;
            this.username = user.getUsername();
            if (user.getContact() != null) {
                this.email = user.getContact().getEmail();
            }
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public char getGenderCode() {
        return genderCode;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
