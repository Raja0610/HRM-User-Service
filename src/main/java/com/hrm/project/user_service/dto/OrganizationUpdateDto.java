package com.hrm.project.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO used for updating organization details.
 *
 * @param name unique organization name
 * @param displayName organization display name
 * @param active organization status
 * @param totalWorkForce total workforce count
 * @param website organization website
 * @param headQuarter organization headquarters location
 * @param establishedYear organization establishment date
 */
public record OrganizationUpdateDto(

        @NotBlank(message = "Organization name is required")
        @Size(max = 255, message = "Organization name cannot exceed 255 characters")
        String name,

        @NotBlank(message = "Organization display name is required")
        @Size(max = 255, message = "Organization display name cannot exceed 255 characters")
        String displayName,

        boolean active,

        @Min(value = 0, message = "Total workforce cannot be negative")
        Long totalWorkForce,

        @Size(max = 500, message = "Website cannot exceed 500 characters")
        String website,

        @Size(max = 255, message = "Headquarter cannot exceed 255 characters")
        String headQuarter,

        @PastOrPresent(message = "Established year cannot be in the future")
        LocalDate establishedYear

) {
}