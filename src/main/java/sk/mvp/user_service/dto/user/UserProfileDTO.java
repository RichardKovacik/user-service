package sk.mvp.user_service.dto.user;

import jakarta.validation.constraints.Size;
import sk.mvp.user_service.dto.ConcatDTO;
import sk.mvp.user_service.model.User;
import sk.mvp.user_service.projections.UserSummaryProjection;

import java.io.Serializable;

public class UserProfileDTO implements Serializable {
    private String username;

    @Size(min = 3, max = 50)
    private String firstName;
    @Size(min = 3, max = 60)
    private String lastName;

    private String genderCode;

    private ConcatDTO concat;

    public UserProfileDTO() {
    }

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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(@Size(min = 3, max = 50) String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(@Size(min = 3, max = 60) String lastName) {
        this.lastName = lastName;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public void setConcat(ConcatDTO concat) {
        this.concat = concat;
    }
}
