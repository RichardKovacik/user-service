package sk.mvp.user_service.common.exception;

public class OutboxNotFoundException extends RuntimeException {
    public OutboxNotFoundException(String message) {
        super(message);
    }
}
