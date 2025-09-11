package sk.mvp.user_service.dto;

import java.io.Serializable;

public class ErrorDetailsDTO  implements Serializable {
    private final String message;

    private final long timestamp;

    public ErrorDetailsDTO(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
