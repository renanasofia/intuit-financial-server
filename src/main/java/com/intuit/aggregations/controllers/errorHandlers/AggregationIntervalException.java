package com.intuit.aggregations.controllers.errorHandlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class AggregationIntervalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AggregationIntervalException(String message) {
        super(message);
    }

    public AggregationIntervalException(String message, Throwable cause) {
        super(message, cause);
    }
}