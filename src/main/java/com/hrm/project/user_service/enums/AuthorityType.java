package com.hrm.project.user_service.enums;

/**
 * System authorities available for assignment to roles.
 * These values are seeded into the database when the application starts.
 */
public enum AuthorityType {
    ORGANIZATION_CREATE("Create organizations"),
    ORGANIZATION_READ("View organizations"),
    ORGANIZATION_UPDATE("Update organizations"),
    ORGANIZATION_DELETE("Delete organizations"),

    BRANCH_CREATE("Create branches"),
    BRANCH_READ("View branches"),
    BRANCH_UPDATE("Update branches"),
    BRANCH_DELETE("Delete branches"),

    DEPARTMENT_CREATE("Create departments"),
    DEPARTMENT_READ("View departments"),
    DEPARTMENT_UPDATE("Update departments"),
    DEPARTMENT_DELETE("Delete departments"),

    USER_CREATE("Create organization users"),
    USER_READ("View organization users"),
    USER_UPDATE("Update organization users"),
    USER_DELETE("Delete organization users"),

    ROLE_CREATE("Create roles"),
    ROLE_READ("View roles"),
    ROLE_UPDATE("Update roles"),
    ROLE_DELETE("Delete roles"),

    AUTHORITY_READ("View authorities"),
    AUTHORITY_DELETE("Delete authorities");

    private final String description;

    AuthorityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
