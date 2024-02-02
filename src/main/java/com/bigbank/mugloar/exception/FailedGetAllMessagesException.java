package com.bigbank.mugloar.exception;

public class FailedGetAllMessagesException extends RuntimeException {

    public FailedGetAllMessagesException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedGetAllMessagesException(String message) {
        super(message);
    }
}
