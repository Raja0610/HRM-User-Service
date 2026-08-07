package com.hrm.project.user_service.service;

import com.hrm.project.user_service.dto.DepartmentDto;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

/**
 * Service contract responsible for Department management operations.
 *
 * <p>
 * Provides functionality for:
 * <ul>
 *     <li>Creating departments under a branch</li>
 *     <li>Retrieving departments with filtering and pagination support</li>
 *     <li>Updating existing departments</li>
 *     <li>Deleting departments</li>
 * </ul>
 * </p>
 */
public interface DepartmentService {

    /**
     * Creates a new department under the specified branch.
     *
     * @param branchId      branch identifier
     * @param departmentDto department creation request
     * @return created department
     */
    DepartmentDto createDepartment(UUID branchId,
                                   @Valid DepartmentDto departmentDto
    );

    /**
     * Retrieves departments belonging to a branch.
     *
     * <p>
     * Supports optional filtering by:
     * <ul>
     *     <li>Department Id</li>
     *     <li>Department Name</li>
     * </ul>
     * <p>
     * Also supports pagination and sorting.
     * </p>
     *
     * @param branchId   branch identifier
     * @param id         optional department identifier
     * @param name       optional department name
     * @param pageNumber page number
     * @param pageSize   page size
     * @param sortBy     sorting field
     * @return filtered department response
     */
    Map<String, Object> getAllDepartments(UUID branchId,
                                          UUID id,
                                          String name,
                                          int pageNumber,
                                          Integer pageSize,
                                          String sortBy
    );

    /**
     * Updates an existing department.
     *
     * @param branchId      branch identifier
     * @param id            department identifier
     * @param departmentDto updated department details
     * @return updated department
     */
    DepartmentDto updateDepartment(UUID branchId,
                                   UUID id,
                                   @Valid DepartmentDto departmentDto
    );

    /**
     * Deletes an existing department.
     *
     * @param branchId branch identifier
     * @param id       department identifier
     */
    void deleteDepartment(UUID branchId,
                          UUID id
    );
}