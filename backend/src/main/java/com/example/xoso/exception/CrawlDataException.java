package com.example.xoso.exception;

public class CrawlDataException extends BaseXosoException {
    public CrawlDataException(String message) {
        super(message);
    }

    public CrawlDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
