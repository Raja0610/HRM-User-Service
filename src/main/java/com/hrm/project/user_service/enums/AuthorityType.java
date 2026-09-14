package com.hrm.project.user_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/**
 * System authorities available for assignment to roles.
 * These values are seeded into the database when the application starts.
 */

@Getter
public enum AuthorityType {
    ORGANIZATION_CREATE("Create organizations", "organization"),
    ORGANIZATION_READ("View organizations", "organization"),
    ORGANIZATION_UPDATE("Update organizations", "organization"),
    ORGANIZATION_DELETE("Delete organizations", "organization"),

    BRANCH_CREATE("Create branches", "branch"),
    BRANCH_READ("View branches", "branch"),
    BRANCH_UPDATE("Update branches", "branch"),
    BRANCH_DELETE("Delete branches", "branch"),

    DEPARTMENT_CREATE("Create departments", "department"),
    DEPARTMENT_READ("View departments", "department"),
    DEPARTMENT_UPDATE("Update departments", "department"),
    DEPARTMENT_DELETE("Delete departments", "departmnet"),

    USER_CREATE("Create organization users", "user"),
    USER_READ("View organization users", "user"),
    USER_UPDATE("Update organization users", "user"),
    USER_DELETE("Delete organization users", "user"),

    ROLE_CREATE("Create roles", "role"),
    ROLE_READ("View roles", "role"),
    ROLE_UPDATE("Update roles", "role"),
    ROLE_DELETE("Delete roles", "role"),

    AUTHORITY_READ("View authorities", "authority"),
    AUTHORITY_DELETE("Delete authorities", "authority");

    private final String type;
    private final String module;

    private AuthorityType(String type, String module) {
        this.type = type;
        this.module = module;
    }

    @JsonCreator
    public static AuthorityType getTypeFromAuthorityType(String type){
        for(AuthorityType authorityType : AuthorityType.values()){
            if(authorityType.getType().equalsIgnoreCase(type)){
                return authorityType;
            }
        }
        return null;
    }
}
