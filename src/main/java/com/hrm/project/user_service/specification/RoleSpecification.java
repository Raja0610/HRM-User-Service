package com.hrm.project.user_service.specification;

import com.hrm.project.user_service.entity.Role;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/** Reusable dynamic filters for organization roles. */
public final class RoleSpecification {

    private RoleSpecification() {
    }

    public static Specification<Role> belongsToOrganization(UUID organizationId) {
        return (root, query, cb) -> cb.equal(root.get("organization").get("id"), organizationId);
    }

    public static Specification<Role> hasId(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static Specification<Role> hasName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Role> hasDisplayName(String displayName) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("displayName")),
                "%" + displayName.toLowerCase() + "%"
        );
    }
}
