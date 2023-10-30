package com.heb.image.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ClientException extends RuntimeException {
    private HttpStatus code;
    private String message;
}
