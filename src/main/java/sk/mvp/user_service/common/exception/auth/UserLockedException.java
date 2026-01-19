package sk.mvp.user_service.common.exception.auth;

import org.springframework.security.core.AuthenticationException;

/**
 * exception thrown when user is locked because of too many login attempts
 */
public class UserLockedException extends AuthenticationException {
    public UserLockedException(String message) {
        super(message);
    }
}
