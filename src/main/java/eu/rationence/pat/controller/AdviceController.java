package eu.rationence.pat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceController {
    private static final String ERROR_STR = "ERROR: ";

    public static ResponseEntity<String> response(HttpStatus httpStatus, String message) {
        if (httpStatus == HttpStatus.OK || httpStatus == HttpStatus.CREATED || httpStatus == HttpStatus.ACCEPTED || httpStatus == HttpStatus.NO_CONTENT)
            return ResponseEntity.status(httpStatus).body(message);
        return ResponseEntity.status(httpStatus).body(ERROR_STR + message);
    }

    public static ResponseEntity<String> responseOk(String message) {
        return response(HttpStatus.OK, message);
    }

    public static ResponseEntity<String> responseCreated(String message) {
        return response(HttpStatus.CREATED, message);
    }

    public static ResponseEntity<String> responseBadRequest(String message) {
        return response(HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseEntity<String> responseNotFound(String message) {
        return response(HttpStatus.NOT_FOUND, message + " (Not found.)");
    }

    public static ResponseEntity<String> responseServerError(String message) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static ResponseEntity<String> responseForbidden(String message) {
        return response(HttpStatus.FORBIDDEN, message + " (Forbidden.)");
    }

    public static ResponseEntity<String> responseConflict(String message) {
        return response(HttpStatus.CONFLICT, message);
    }

    public static boolean isStringPositiveDecimal(String string) {
        if (string == null)
            return false;
        for (char c : string.toCharArray())
            if (!Character.isDigit(c)) return false;
        return true;
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindingExceptionException(BindException e) {
        return responseBadRequest("Empty input or mismatched input type");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return responseBadRequest(e.getMessage());
    }
}
