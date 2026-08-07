package com.hrm.project.user_service.service;

import com.hrm.project.user_service.dto.OrganizationUserDto;
import com.hrm.project.user_service.dto.UserDto;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing users within an organization.
 * Provides operations for assigning, updating, retrieving,
 * and removing organization users.
 */
public interface OrganizationUserService {

    /**
     * Assigns a user to the specified organization.
     *
     * @param organizationId the organization identifier
     * @param organizationUserDto the user details
     * @return the created organization-user association
     */
    OrganizationUserDto assignUserToOrganization(UUID organizationId, @Valid UserDto organizationUserDto);

    /**
     * Updates an existing user within the specified organization.
     *
     * @param organizationId the organization identifier
     * @param userId the user identifier
     * @param organizationUserDto the updated user details
     * @return the updated organization-user association
     */
    OrganizationUserDto updateOrganizationUser(UUID organizationId, UUID userId, @Valid UserDto organizationUserDto);

    /**
     * Retrieves users belonging to the specified organization with optional filters.
     *
     * @param organizationId the organization identifier
     * @param userName optional username filter
     * @param email optional email filter
     * @param branchId optional branch identifier
     * @param departmentId optional department identifier
     * @param pageNumber page number
     * @param pageSize page size
     * @return a map containing the paginated list of organization users and pagination details
     */
    Map<String, Object> getOrganizationUsers(UUID organizationId,
                                             String userName,
                                             String email,
                                             UUID branchId,
                                             UUID departmentId,
                                             int pageNumber,
                                             Integer pageSize);

    /**
     * Removes a user from the specified organization.
     *
     * @param organizationId the organization identifier
     * @param userId the user identifier
     */
    void removeUserFromOrganization(UUID organizationId, UUID userId);
}
