package sk.mvp.user_service.common.exception;

public class KafkaProductionException extends RuntimeException {
    public KafkaProductionException(String message, Throwable cause) {
        super(message, cause);
    }
}
