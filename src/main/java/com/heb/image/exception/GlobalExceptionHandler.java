package com.heb.image.exception;

import com.heb.image.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(value = ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException exception, WebRequest request) {
        log.log(Level.WARNING, "exception caught", exception);
        return new ResponseEntity<>(new ErrorResponse(exception.getCode().value(), exception.getMessage()), exception.getCode());
    }

    @ExceptionHandler(value = ImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleImageNotFoundException(ImageNotFoundException exception, WebRequest request) {
        log.log(Level.WARNING, "exception caught", exception);
        return new ResponseEntity<>(new ErrorResponse(exception.getCode().value(), exception.getMessage()), exception.getCode());
    }

    @ExceptionHandler(value = ImaggaServerException.class)
    public ResponseEntity<ErrorResponse> handleImaggaException(ImaggaServerException exception, WebRequest request) {
        log.log(Level.SEVERE, "exception caught", exception);
        return new ResponseEntity<>(new ErrorResponse(exception.getCode(), exception.getMessage()), HttpStatus.valueOf(exception.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, WebRequest request) {
        log.log(Level.SEVERE, "exception caught", exception);
        return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR.value(), exception.getMessage()), INTERNAL_SERVER_ERROR);
    }
}
