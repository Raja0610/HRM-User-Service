package com.hrm.project.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object representing a user associated with an organization.
 * Contains organization details along with the corresponding user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUserDto {

    /** Unique identifier of the organization. */
    private UUID organizationId;

    /** Name of the organization. */
    private String organizationName;

    /** User details associated with the organization. */
    private UserDto user;
}