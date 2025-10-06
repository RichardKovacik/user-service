package sk.mvp.user_service.dto.user;

import sk.mvp.user_service.model.User;
import sk.mvp.user_service.projections.UserSummaryProjection;

import java.io.Serializable;

public class UserSummaryDTO implements Serializable {
    private String username;
    private String email;
    private String lastName;
    private String genderCode;

    public UserSummaryDTO(UserSummaryProjection userSumaryData) {
        if (userSumaryData != null) {
            this.lastName = userSumaryData.getLastName();
            this.genderCode = userSumaryData.getGender().getCodeAsString();
            this.email = null;
            this.username = userSumaryData.getUsername();
            if (userSumaryData.getContact() != null) {
                this.email = userSumaryData.getContact().getEmail();
            }
        }
    }

    public String getLastName() {
        return lastName;
    }

    public String getGenderCode() {
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
