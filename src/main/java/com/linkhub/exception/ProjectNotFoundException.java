package com.linkhub.exception;

public class ProjectNotFoundException extends ApiException {

    public ProjectNotFoundException(String message) {
        super(message);
    }
}