package com.linkhub.exception;

public class JobNotFoundException extends ApiException {

    public JobNotFoundException(String message) {
        super(message);
    }
}