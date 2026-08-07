package com.hrm.project.user_service.exceptions;

public class DependentResourceDeleteException extends RuntimeException {
    public DependentResourceDeleteException(String s) {
        super(s);
    }
}
