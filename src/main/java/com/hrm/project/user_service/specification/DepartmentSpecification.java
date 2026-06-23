package com.hrm.project.user_service.specification;

import com.hrm.project.user_service.entity.Department;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Specification utility class for building dynamic Department queries.
 *
 * <p>
 * Provides reusable specifications for:
 * <ul>
 *     <li>Filtering by department id</li>
 *     <li>Filtering by department name</li>
 *     <li>Filtering by branch</li>
 * </ul>
 * </p>
 *
 * <p>
 * These specifications are typically combined using
 * {@code Specification.and()} in the service layer to support
 * dynamic searching and filtering.
 * </p>
 */
public final class DepartmentSpecification {

    /**
     * Prevent instantiation.
     */
    private DepartmentSpecification() {
    }

    /**
     * Filters departments belonging to a specific branch.
     *
     * @param branchId branch identifier
     * @return specification for branch filtering
     */
    public static Specification<Department> belongsToBranch(UUID branchId) {

        return (root, query, cb) ->
                cb.equal(
                        root.get("branch").get("id"),
                        branchId
                );
    }

    /**
     * Filters department by its unique identifier.
     *
     * @param id department identifier
     * @return specification for department id filtering
     */
    public static Specification<Department> hasId(UUID id) {

        return (root, query, cb) ->
                cb.equal(
                        root.get("id"),
                        id
                );
    }

    /**
     * Filters departments by name.
     *
     * <p>
     * Performs a case-insensitive partial match.
     * </p>
     *
     * Example:
     * <pre>
     * name = "eng"
     * matches:
     * Engineering
     * Software Engineering
     * </pre>
     *
     * @param name department name
     * @return specification for name filtering
     */
    public static Specification<Department> hasName(String name) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}