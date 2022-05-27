package eu.Rationence.pat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceController {
    private static final String ERROR_STR = "ERROR: ";

    public static ResponseEntity<String> responseBadRequest(String message){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ERROR_STR + message);
    }

    public static ResponseEntity<String> responseNotFound(String message){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ERROR_STR + message);
    }

    public static ResponseEntity<String> responseOk(String message){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(message);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindingExceptionException(BindException e) {
        return responseBadRequest("Empty input or mismatched input type");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return responseBadRequest(e.getMessage());
    }
}
