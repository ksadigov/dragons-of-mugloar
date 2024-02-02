package com.bigbank.mugloar.exception;

public class FailedRunInvestigationException extends RuntimeException {

    public FailedRunInvestigationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedRunInvestigationException(String message) {
        super(message);
    }
}
