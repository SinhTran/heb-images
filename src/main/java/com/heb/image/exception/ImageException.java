package com.heb.image.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ImageException extends RuntimeException {

    private String message;
    private HttpStatus code;
    public ImageException(Throwable throwable) {
        super(throwable);
        this.code = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = throwable.getMessage();
    }

    public ImageException(Throwable throwable, HttpStatus code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

}
