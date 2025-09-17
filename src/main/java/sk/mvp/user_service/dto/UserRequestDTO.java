package sk.mvp.user_service.dto;

public class UserRequestDTO {
    private String username;
    private String password;
    private String email;
    private char genderCode;// F or M
    // TODO: dorobit validaciu vstupu registracie tieto 4 paramtre povinne spring bean validation

    public UserRequestDTO(String username, String password, String email, char genderCode) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.genderCode = genderCode;
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

    public char getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(char genderCode) {
        this.genderCode = genderCode;
    }
}
