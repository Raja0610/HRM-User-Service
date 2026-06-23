package com.hrm.project.user_service.repository;

import com.hrm.project.user_service.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for Department persistence operations.
 *
 * <p>
 * Provides:
 * <ul>
 *     <li>Basic CRUD operations through {@link JpaRepository}</li>
 *     <li>Dynamic filtering support through {@link JpaSpecificationExecutor}</li>
 *     <li>Department-specific existence and lookup operations</li>
 * </ul>
 * </p>
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID>,
        JpaSpecificationExecutor<Department> {

    /**
     * Checks whether a department already exists with the given name
     * within a specific branch.
     *
     * <p>
     * Recommended uniqueness validation for branch-level departments.
     * Two branches may contain departments with the same name,
     * but a single branch cannot.
     * </p>
     *
     * @param name     department name
     * @param branchId branch identifier
     * @return true if department exists within the branch
     */
    boolean existsByNameIgnoreCaseAndBranchId(
            String name,
            UUID branchId
    );

    /**
     * Retrieves a department belonging to a specific branch.
     *
     * @param id       department identifier
     * @param branchId branch identifier
     * @return matching department if found
     */
    Optional<Department> findByIdAndBranchId(
            UUID id,
            UUID branchId
    );

    /**
     * Checks whether another department exists with the same name
     * within the same branch while excluding the current department.
     *
     * <p>
     * Used during update operations.
     * </p>
     *
     * @param name     department name
     * @param branchId branch identifier
     * @param id       current department identifier
     * @return true if another department exists with the same name
     */
    boolean existsByNameIgnoreCaseAndBranchIdAndIdNot(
            String name,
            UUID branchId,
            UUID id
    );
}