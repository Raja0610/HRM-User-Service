package com.hrm.project.user_service.service;

import com.hrm.project.user_service.dto.OrganizationDto;
import com.hrm.project.user_service.dto.OrganizationUpdateDto;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

/**
 * Service contract for Organization management operations.
 * <p>
 * Responsible for handling organization lifecycle actions such as:
 * <ul>
 *     <li>Organization creation</li>
 *     <li>Organization retrieval with filtering and pagination support</li>
 *     <li>Organization updates</li>
 *     <li>Organization deletion</li>
 * </ul>
 */
public interface OrganizationService {

    /**
     * Creates a new organization.
     *
     * @param organizationDto details of the organization to be created
     * @return newly created organization details
     */
    OrganizationDto createOrganization(@Valid OrganizationDto organizationDto);

    /**
     * Retrieves organization data based on the provided search criteria.
     * <p>
     * Supports:
     * <ul>
     *     <li>Fetch by organization identifier</li>
     *     <li>Fetch by short name</li>
     *     <li>Paginated listing</li>
     *     <li>Sorting</li>
     * </ul>
     *
     * @param id         optional organization identifier
     * @param shortName  optional organization short name
     * @param pageNumber page number for pagination
     * @param pageSize   number of records per page
     * @param sortBy     field used for sorting results
     * @return response containing organization data and pagination metadata
     */
    Map<String, Object> getAllOrganizations(UUID id,
                                            String shortName,
                                            int pageNumber,
                                            Integer pageSize,
                                            String sortBy);

    /**
     * Updates an existing organization.
     *
     * @param id                    organization identifier
     * @param organizationUpdateDto updated organization details
     * @return updated organization information
     */
    OrganizationDto updateOrganization(UUID id,
                                       @Valid OrganizationUpdateDto organizationUpdateDto);

    /**
     * Deletes an organization from the system.
     *
     * @param id organization identifier
     */
    void deleteOrganization(UUID id);
}