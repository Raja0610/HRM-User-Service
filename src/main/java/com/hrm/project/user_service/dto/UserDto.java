package com.hrm.project.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing user information.
 *
 * @param id Unique identifier of the user.
 * @param firstName User's first name.
 * @param lastName User's last name.
 * @param mobileNumber User's mobile number. Must be a valid 10-digit Indian mobile number.
 * @param email User's email address.
 * @param organizationId Identifier of the organization to which the user belongs.
 * @param organizationName Name of the organization associated with the user.
 * @param branchId Identifier of the user's branch.
 * @param departmentId Identifier of the user's department.
 */
public record UserDto(

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

        UUID organizationId,

        String organizationName,

        UUID branchId,

        UUID departmentId,

        Set<UUID> roleIds

) {

    /** Retains compatibility for callers that do not assign roles. */
    public UserDto(UUID id, String firstName, String lastName, String mobileNumber, String email,
                   UUID organizationId, String organizationName, UUID branchId, UUID departmentId) {
        this(id, firstName, lastName, mobileNumber, email, organizationId, organizationName,
                branchId, departmentId, Set.of());
    }
}
