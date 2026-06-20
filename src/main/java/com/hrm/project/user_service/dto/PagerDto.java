package com.hrm.project.user_service.dto;

/**
 * DTO containing pagination metadata.
 *
 * @param totalElements total number of records matching the search criteria
 * @param totalPages total number of available pages
 */
public record PagerDto(
        long totalElements,
        int totalPages
) {
}