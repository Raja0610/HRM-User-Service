package com.hrm.project.user_service.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException( String source, String id, Object value) {
        super(createMessage(source, id, value));
    }
    private static String createMessage(String source, String id, Object value){
        return String.format("%s already exists with %s: %s", source, id, value.toString());
    }
}
