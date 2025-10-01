package sk.mvp.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sk.mvp.user_service.dto.ErrorType;
import sk.mvp.user_service.dto.QErrorResponseDTO;
import sk.mvp.user_service.dto.Error;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // handle custom application exceptions
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<QErrorResponseDTO> handleCustomAplicationException(ApplicationException ex, HttpServletRequest request) {
        return createQErrorResponse(ex.getErrorType(), ex.getMessage(), request.getRequestURI(), ex.getData());
    }

    // handle default runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<QErrorResponseDTO> handleOtherRuntime(RuntimeException ex, HttpServletRequest request) {
        return createQErrorResponse(ErrorType.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI(), null);
    }

    // Java DTO validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<QErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, Object> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return createQErrorResponse(ErrorType.INPUT_VALIDATION_ERROR,
                "Input Validation exception",
                request.getRequestURI(),
                fieldErrors);

    }


    private ResponseEntity<QErrorResponseDTO> createQErrorResponse(ErrorType errorType, String message, String path,
                                                                   Map<String, Object> data) {
        Error error = new Error(errorType, message, path, data);
        return new ResponseEntity<>(new QErrorResponseDTO(error), new HttpHeaders(), error.getStatusCode());
    }
}
