package com.hrm.project.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO used for organization create and fetch operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDto {

    private UUID id;

    @NotBlank(message = "Organization name must not be blank")
    @Size(max = 255, message = "Organization name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Display name must not be blank")
    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;

    private boolean active;

    private Long totalWorkForce;

    @NotBlank(message = "Website must not be blank")
    @Size(max = 500, message = "Website must not exceed 500 characters")
    @Pattern(
            regexp = "^(https?://).+$",
            message = "Website must start with http:// or https://"
    )
    private String website;

    private String headQuarter;

    @PastOrPresent(message = "Established year must be in the past or present")
    private LocalDate establishedYear;
}