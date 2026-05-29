package com.igor.sistema_hospitalar.domain.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}
