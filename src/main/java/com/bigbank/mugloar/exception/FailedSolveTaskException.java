package com.bigbank.mugloar.exception;

public class FailedSolveTaskException extends RuntimeException {

    public FailedSolveTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedSolveTaskException(String message) {
        super(message);
    }
}
