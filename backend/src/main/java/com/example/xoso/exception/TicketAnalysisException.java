package com.example.xoso.exception;

public class TicketAnalysisException extends BaseXosoException {
    public TicketAnalysisException(String message) {
        super(message);
    }

    public TicketAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
