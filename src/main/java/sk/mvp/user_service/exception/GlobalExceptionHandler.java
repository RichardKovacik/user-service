package sk.mvp.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sk.mvp.user_service.dto.ErrorDetailsDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetailsDTO> handleAsNotFound(RuntimeException ex) {
        ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO(ex.getMessage());
        return new ResponseEntity<>(errorDetailsDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidGenderException.class, EmailAlreadyInUseException.class})
    public ResponseEntity<ErrorDetailsDTO> handleAsBadRequest(RuntimeException ex) {
        ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO(ex.getMessage());
        return new ResponseEntity<>(errorDetailsDTO, HttpStatus.BAD_REQUEST);
    }
}
