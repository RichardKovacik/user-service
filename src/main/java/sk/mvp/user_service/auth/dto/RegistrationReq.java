package sk.mvp.user_service.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationReq {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 3, max = 50)
    private String password;

    private String email;

    @Pattern(regexp = "^[FM]$", message = "Gender code must be F or M")
    private String genderCode;// F or M
    // TODO: dorobit validaciu vstupu registracie tieto 4 paramtre povinne spring bean validation

    public RegistrationReq(String username, String password, String email, String genderCode) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.genderCode = genderCode;
    }

    public RegistrationReq() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenderCode() {
        return genderCode;
    }
    public char getGenderCodeAsCharacter() {
        return Character.toUpperCase(genderCode.charAt(0));
    }
}
