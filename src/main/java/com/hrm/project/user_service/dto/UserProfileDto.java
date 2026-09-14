package com.hrm.project.user_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * Personal information maintained for a user independently of an organization assignment.
 */
public record UserProfileDto(
        UUID id,

        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Mobile number must be a valid 10 digit Indian mobile number"
        )
        String mobileNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Valid
        AddressDto address
) {
}
