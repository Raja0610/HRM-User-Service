package com.hrm.project.user_service.specification;

import com.hrm.project.user_service.entity.Organization;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class OrganizationSpecification {

    private OrganizationSpecification() {
    }

    /**
     * Filters organizations by id.
     *
     * @param id organization identifier
     * @return specification for id filter
     */
    public static Specification<Organization> hasId(UUID id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }

    /**
     * Filters organizations by name.
     *
     * @param name organization name
     * @return specification for name filter
     */
    public static Specification<Organization> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%"
                );
    }

    /**
     * Filters organizations by name.
     *
     * @param displayName organization name
     * @return specification for name filter
     */
    public static Specification<Organization> hasDisplayName(String displayName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("displayName"), "%"+ displayName + "%");
    }
}