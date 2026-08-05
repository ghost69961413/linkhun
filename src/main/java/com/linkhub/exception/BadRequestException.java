package com.linkhub.exception;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(message);
    }

}