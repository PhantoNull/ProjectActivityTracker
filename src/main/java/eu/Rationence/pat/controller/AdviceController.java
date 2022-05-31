package eu.Rationence.pat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceController {
    private static final String ERROR_STR = "ERROR: ";

    public static ResponseEntity<String> response(HttpStatus httpStatus, String message){
        if(httpStatus == HttpStatus.OK ||  httpStatus == HttpStatus.CREATED || httpStatus == HttpStatus.ACCEPTED || httpStatus == HttpStatus.NO_CONTENT)
            return ResponseEntity.status(httpStatus).body(message);
        return ResponseEntity.status(httpStatus).body(ERROR_STR + message);
    }

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

    public static ResponseEntity<String> responseCreated(String message){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(message);
    }

    public static ResponseEntity<String> responseServerError(String message){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }

    public static ResponseEntity<String> responseForbidden(String message){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(message);
    }

    public static ResponseEntity<String> responseConflict(String message){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindingExceptionException(BindException e) {
        return responseBadRequest("Empty input or mismatched input type");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        System.out.println(e);
        return responseBadRequest(e.getMessage());
    }
}
