package com.hrm.project.user_service.specification;

import com.hrm.project.user_service.entity.OrganizationUser;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Utility class containing JPA specifications for filtering
 * {@link OrganizationUser} entities based on various search criteria.
 */
public final class OrganizationUserSpecification {

    /**
     * Prevents instantiation of this utility class.
     */
    private OrganizationUserSpecification() {
    }

    /**
     * Creates a specification to filter users by organization identifier.
     *
     * @param organizationId the organization identifier
     * @return specification matching the organization
     */
    public static Specification<OrganizationUser> hasOrganizationId(UUID organizationId) {
        return (root, query, cb) ->
                cb.equal(root.get("organization").get("id"), organizationId);
    }

    /**
     * Creates a specification to filter users by first name or last name.
     * The search is case-insensitive and supports partial matching.
     *
     * @param userName the username to search for
     * @return specification matching the username
     */
    public static Specification<OrganizationUser> hasUserName(String userName) {

        return (root, query, cb) -> cb.or(
                cb.like(
                        cb.lower(root.get("user").get("firstName")),
                        "%" + userName.toLowerCase() + "%"
                ),
                cb.like(
                        cb.lower(root.get("user").get("lastName")),
                        "%" + userName.toLowerCase() + "%"
                )
        );
    }

    /**
     * Creates a specification to filter users by email address.
     * The search is case-insensitive and supports partial matching.
     *
     * @param email the email address to search for
     * @return specification matching the email
     */
    public static Specification<OrganizationUser> hasEmail(String email) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("user").get("email")),
                        "%" + email.toLowerCase() + "%"
                );
    }

    /**
     * Creates a specification to filter users by branch identifier.
     *
     * @param branchId the branch identifier
     * @return specification matching the branch
     */
    public static Specification<OrganizationUser> hasBranchId(UUID branchId) {
        return (root, query, cb) ->
                cb.equal(root.get("branch").get("id"), branchId);
    }

    /**
     * Creates a specification to filter users by department identifier.
     *
     * @param departmentId the department identifier
     * @return specification matching the department
     */
    public static Specification<OrganizationUser> hasDepartmentId(UUID departmentId) {
        return (root, query, cb) ->
                cb.equal(root.get("department").get("id"), departmentId);
    }
}