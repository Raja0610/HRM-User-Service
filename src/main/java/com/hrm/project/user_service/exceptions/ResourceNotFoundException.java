package com.hrm.project.user_service.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String key, Object value) {
        super(String.format("%s not found with %s: %s", resourceName, key, value));
    }
}
