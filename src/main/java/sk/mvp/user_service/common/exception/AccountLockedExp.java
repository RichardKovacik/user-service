package sk.mvp.user_service.common.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountLockedExp extends AuthenticationException {
    public AccountLockedExp(String message) {
        super(message);
    }
}
