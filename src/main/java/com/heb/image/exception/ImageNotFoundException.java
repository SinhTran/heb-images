package com.heb.image.exception;


import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ImageNotFoundException extends RuntimeException {
    private HttpStatus code;
    private String message;

    public ImageNotFoundException(String message) {
        this.code = HttpStatus.NOT_FOUND;
        this.message = message;
    }

}
