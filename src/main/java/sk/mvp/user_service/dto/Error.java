package sk.mvp.user_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class Error {
    private ErrorType errorType;
    private int statusCode;
    private String message;
    private String path;
    //TODO: zmenit na UTC format
    private LocalDateTime timestamp;
    private Map<String, Object> data;

    public Error(ErrorType errorType, String message, String path, Map<String, Object> data) {
        this.errorType = errorType;
        this.statusCode = errorType.getStatus();
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
