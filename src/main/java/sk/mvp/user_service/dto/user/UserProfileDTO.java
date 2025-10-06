package sk.mvp.user_service.dto.user;

import sk.mvp.user_service.dto.ConcatDTO;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.projections.UserSummaryProjection;

import java.io.Serializable;

public class UserProfileDTO implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String genderCode;
    private ConcatDTO concat;

    public UserProfileDTO(User user) {
        if (user != null) {
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.genderCode = user.getGender().getCodeAsString();
            this.username = user.getUsername();
            if (user.getContact() != null) {
               this.concat = new ConcatDTO(user.getContact().getEmail(), user.getContact().getPhoneNumber());
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public ConcatDTO getConcat() {
        return concat;
    }
}
