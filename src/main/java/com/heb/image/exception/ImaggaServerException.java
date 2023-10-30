package com.heb.image.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImaggaServerException extends RuntimeException{

    private int code;
    private String message;
}
