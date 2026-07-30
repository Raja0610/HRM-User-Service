package com.hrm.project.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** DTO used to create, update, and return organization roles. */
public record RoleDto(
        UUID id,
        @NotBlank(message = "Role name must not be blank")
        @Size(max = 50, message = "Role name must not exceed 50 characters")
        String name,
        @NotBlank(message = "Role display name must not be blank")
        @Size(max = 100, message = "Role display name must not exceed 100 characters")
        String displayName,
        @Size(max = 500, message = "Role description must not exceed 500 characters")
        String description,
        UUID organizationId
) {
}
