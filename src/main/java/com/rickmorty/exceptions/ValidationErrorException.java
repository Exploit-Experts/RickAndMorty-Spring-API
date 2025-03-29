package com.rickmorty.exceptions;

import java.util.List;

public class ValidationErrorException extends RuntimeException {
    private final List<String> errors;

    public ValidationErrorException(List<String> errors) {
        super("Validation error");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
