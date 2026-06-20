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
                id == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("id"), id);
    }

    /**
     * Filters organizations by name.
     *
     * @param name organization name
     * @return specification for name filter
     */
    public static Specification<Organization> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null || name.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}