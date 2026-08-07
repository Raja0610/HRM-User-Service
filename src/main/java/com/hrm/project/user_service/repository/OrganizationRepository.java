package com.hrm.project.user_service.repository;

import com.hrm.project.user_service.entity.Organization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository responsible for Organization persistence operations.
 * <p>
 * Extends:
 * <ul>
 *     <li>{@link JpaRepository} for standard CRUD operations.</li>
 *     <li>{@link JpaSpecificationExecutor} for dynamic filtering using Specifications.</li>
 * </ul>
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID>, JpaSpecificationExecutor<Organization> {

    /**
     * Checks whether an organization exists with the given name,
     * ignoring character case.
     *
     * @param name organization name
     * @return true if an organization exists with the provided name, otherwise false
     */
    boolean existsByNameIgnoreCase(
            @NotBlank(message = "Organization name must not be blank")
            @Size(max = 255, message = "Organization name must not exceed 255 characters")
            String name
    );

    /**
     * Checks whether another organization exists with the given name,
     * excluding the organization identified by the provided id.
     * <p>
     * Primarily used during update operations to enforce name uniqueness.
     *
     * @param name organization name
     * @param id   organization id to exclude from the search
     * @return true if another organization exists with the provided name,
     * otherwise false
     */
    boolean existsByNameIgnoreCaseAndIdNot(
            @NotBlank(message = "Organization name is required")
            @Size(max = 255, message = "Organization name cannot exceed 255 characters")
            String name,
            UUID id
    );
}