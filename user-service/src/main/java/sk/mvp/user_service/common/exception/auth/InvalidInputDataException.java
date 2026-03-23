package sk.mvp.user_service.common.exception.auth;


import org.springframework.security.core.AuthenticationException;

public class InvalidInputDataException extends AuthenticationException {
    public InvalidInputDataException(String message) {
        super(message);
    }
}
