package sk.mvp.user_service.dto;

public enum ErrorType {
    USER_NOT_FOUND(404),
    INPUT_VALIDATION_ERROR(400),
    USER_NAME_DUPLICATED(409),
    EMAIL_DUPLICATED(409),
    INTERNAL_SERVER_ERROR(500);

    private int status;

    ErrorType(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
