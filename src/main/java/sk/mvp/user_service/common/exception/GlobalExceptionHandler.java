package sk.mvp.user_service.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.common.exception.data.QErrorResponse;
import sk.mvp.user_service.common.exception.data.QError;
import sk.mvp.user_service.common.filter.LoggingFilter;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    // handle custom application exceptions
    @ExceptionHandler(QApplicationException.class)
    public ResponseEntity<QErrorResponse> handleCustomAplicationException(QApplicationException ex, HttpServletRequest request) {
        return createQErrorResponse(ex.getErrorType(), ex.getMessage(), request.getRequestURI(), ex.getData());
    }

    // handle default runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<QErrorResponse> handleOtherRuntime(RuntimeException ex, HttpServletRequest request) {
        log.error(ErrorType.INTERNAL_SERVER_ERROR.toString(), ex);
        return createQErrorResponse(ErrorType.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI(), null);
    }

    // Java DTO validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<QErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, Object> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return createQErrorResponse(ErrorType.INPUT_VALIDATION_ERROR,
                "Input Validation exception",
                request.getRequestURI(),
                fieldErrors);

    }

    //TODO: remove message paramter

    private ResponseEntity<QErrorResponse> createQErrorResponse(ErrorType errorType, String message, String path,
                                                                Map<String, Object> data) {
        QError QError = new QError(errorType, path, data);
        return new ResponseEntity<>(new QErrorResponse(QError), new HttpHeaders(), QError.getStatusCode());
    }
}
