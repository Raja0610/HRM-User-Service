package com.hrm.project.user_service.exceptions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(@NotBlank(message = "Organization name must not be blank") @Size(max = 255, message = "Organization name must not exceed 255 characters") String s) {
        super(s);
    }
}
