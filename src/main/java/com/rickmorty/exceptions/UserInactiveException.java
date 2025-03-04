package com.rickmorty.exceptions;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super(message);
    }
    public UserInactiveException() {}
}
