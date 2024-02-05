package com.bigbank.mugloar.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            FailedGetAllMessagesException.class,
            FailedStartNewGameException.class,
            FailedGetAllShopItemsException.class,
            FailedPurchaseItemException.class,
            FailedRunInvestigationException.class,
            FailedSolveTaskException.class
    })
    public ResponseEntity<Object> handleCustomException(RuntimeException ex, WebRequest request) {
        HttpStatus status = determineHttpStatus(ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, status);
    }

    private HttpStatus determineHttpStatus(RuntimeException ex) {
        if (ex instanceof FailedGetAllMessagesException) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (ex instanceof FailedStartNewGameException) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
