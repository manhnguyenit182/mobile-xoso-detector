package com.example.xoso.exception;

public class BaseXosoException extends RuntimeException {
    public BaseXosoException(String message) {
        super(message);
    }

    public BaseXosoException(String message, Throwable cause) {
        super(message, cause);
    }
}
