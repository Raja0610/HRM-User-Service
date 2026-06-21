package com.hrm.project.user_service.specification;

import com.hrm.project.user_service.entity.Branch;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Specification utility class for dynamic Branch filtering.
 * <p>
 * Provides reusable JPA Specifications that can be combined
 * to build flexible search queries for Branch entities.
 * </p>
 *
 * Supported filters:
 * <ul>
 *     <li>Organization ownership</li>
 *     <li>Branch identifier</li>
 *     <li>Branch name</li>
 * </ul>
 */
public final class BranchSpecification {

    /**
     * Private constructor to prevent instantiation.
     */
    private BranchSpecification() {
    }

    /**
     * Filters branches belonging to a specific organization.
     *
     * @param organizationId organization identifier
     * @return specification matching branches under the organization
     */
    public static Specification<Branch> belongsToOrganization(UUID organizationId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("organization").get("id"),
                        organizationId
                );
    }

    /**
     * Filters branch by its identifier.
     *
     * @param id branch identifier
     * @return specification matching the provided branch id
     */
    public static Specification<Branch> hasId(UUID id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("id"),
                        id
                );
    }

    /**
     * Filters branches using a case-insensitive partial name match.
     *
     * Example:
     * <pre>
     * name = "del"
     * Matches:
     * - Delhi Branch
     * - Delhi Head Office
     * </pre>
     *
     * @param name branch name or partial name
     * @return specification matching branch names
     */
    public static Specification<Branch> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}