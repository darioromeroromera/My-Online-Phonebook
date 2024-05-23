package com.rest.pruebarest.exceptions;

public class ForbiddenAccessException extends Exception {
    public ForbiddenAccessException(String message) {
        super(message);
    }
}
