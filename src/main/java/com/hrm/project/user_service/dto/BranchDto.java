package com.hrm.project.user_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record BranchDto(

        UUID id,

        @NotBlank(message = "Branch name must not be blank")
        @Size(max = 255, message = "Branch name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Display name must not be blank")
        @Size(max = 255, message = "Display name must not exceed 255 characters")
        String displayName,

        boolean active,

        UUID organizationId,

        String organizationName,

        @Email(message = "Please provide a valid email address")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Pattern(
                regexp = "^[0-9+\\-() ]{7,20}$",
                message = "Please provide a valid phone number"
        )
        String phone,

        @Valid
        AddressDto address,

        Long workForce,

        Map<String, Object> specialAttributes,

        @PastOrPresent(message = "Established date must be in the past or present")
        LocalDate establishedIn

) {
}