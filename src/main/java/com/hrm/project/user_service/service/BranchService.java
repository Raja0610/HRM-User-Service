package com.hrm.project.user_service.service;

import com.hrm.project.user_service.dto.BranchDto;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for Branch management operations.
 */
public interface BranchService {

    /**
     * Creates a new branch under the specified organization.
     *
     * @param organizationId organization identifier
     * @param branchDto branch creation request
     * @return created branch
     */
    BranchDto createBranch(UUID organizationId, @Valid BranchDto branchDto);

    /**
     * Retrieves branches using optional filters and pagination.
     *
     * @param organizationId organization identifier
     * @param id branch identifier
     * @param name branch name
     * @param pageNumber page number
     * @param pageSize page size
     * @param sortBy field used for sorting
     * @return response containing pager and items
     */
    Map<String, Object> getAllBranches(UUID organizationId, UUID id, String name, int pageNumber, Integer pageSize, String sortBy);

    /**
     * Updates an existing branch.
     *
     * @param organizationId organization identifier
     * @param id branch identifier
     * @param branchDto updated branch details
     * @return updated branch
     */
    BranchDto updateBranch(UUID organizationId, UUID id, @Valid BranchDto branchDto);

    /**
     * Deletes an existing branch.
     *
     * @param organizationId organization identifier
     * @param id branch identifier
     */
    void deleteBranch(UUID organizationId, UUID id);
}