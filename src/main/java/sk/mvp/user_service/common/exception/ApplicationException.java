package sk.mvp.user_service.common.exception;

import sk.mvp.user_service.common.exception.data.ErrorType;

import java.util.Map;

public class ApplicationException extends RuntimeException {
    private ErrorType errorType;
    private Map<String, Object> data;

    public ApplicationException(String message, ErrorType errorType, Map<String, Object> data) {
        super(message);
        this.errorType = errorType;
        this.data = data;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
