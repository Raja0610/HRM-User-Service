package com.hrm.project.user_service.repository;

import com.hrm.project.user_service.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for Branch persistence operations.
 *
 * <p>
 * Provides:
 * <ul>
 *     <li>Basic CRUD operations through {@link JpaRepository}</li>
 *     <li>Dynamic filtering support through {@link JpaSpecificationExecutor}</li>
 *     <li>Branch-specific existence and lookup operations</li>
 * </ul>
 * </p>
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID>, JpaSpecificationExecutor<Branch> {


    /**
     * Checks whether a branch already exists with the given name
     * within a specific organization.
     *
     * <p>
     * Recommended uniqueness validation for multi-tenant systems.
     * Two organizations may have branches with the same name,
     * but a single organization cannot.
     * </p>
     *
     * @param name           branch name
     * @param organizationId organization identifier
     * @return true if branch exists within the organization
     */
    boolean existsByNameIgnoreCaseAndOrganizationId(String name, UUID organizationId);

    /**
     * Retrieves a branch belonging to a specific organization.
     *
     * @param id             branch identifier
     * @param organizationId organization identifier
     * @return matching branch if found
     */
    Optional<Branch> findByIdAndOrganizationId(UUID id, UUID organizationId);

    /**
     * Checks whether another branch exists with the same name
     * within the same organization while excluding the current branch.
     *
     * <p>
     * Used during update operations.
     * </p>
     *
     * @param name           branch name
     * @param organizationId organization identifier
     * @param id             current branch identifier
     * @return true if another branch exists with the same name
     */
    boolean existsByNameIgnoreCaseAndOrganizationIdAndIdNot(
            String name,
            UUID organizationId,
            UUID id
    );
}