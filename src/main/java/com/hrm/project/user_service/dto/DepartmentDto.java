package com.hrm.project.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

/**
 * DTO used for department creation, update and retrieval operations.
 *
 * @param id                department identifier
 * @param name              department name
 * @param displayName       user-friendly department name
 * @param active            department status
 * @param branchId          parent branch identifier
 * @param branchName        parent branch name
 * @param workforce         workforce information
 * @param specialAttributes dynamic custom attributes
 */
public record DepartmentDto(

        UUID id,

        /*
         * Department name.
         */
        @NotBlank(message = "Department name must not be blank")
        @Size(max = 100, message = "Department name must not exceed 100 characters")
        String name,

        /*
         * Display name shown in UI.
         */
        @NotBlank(message = "Display name must not be blank")
        @Size(max = 150, message = "Display name must not exceed 150 characters")
        String displayName,

        /*
         * Active status.
         */
        @NotNull(message = "Department status is required")
        Boolean active,

        /*
         * Parent branch identifier.
         */
        UUID branchId,

        /*
         * Parent branch name.
         */
        String branchName,

        /*
         * Workforce information.
         */
        Long workforce,

        /*
         * Dynamic department-specific attributes.
         *
         * Example:
         * {
         *   "shiftType": "Day",
         *   "costCenter": "CC-1001"
         * }
         */
        Map<String, Object> specialAttributes

) {
}