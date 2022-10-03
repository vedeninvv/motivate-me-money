package com.money.me.motivate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PasswordNotCorrectException extends RuntimeException{
    public PasswordNotCorrectException(String message) {
        super(message);
    }
}
