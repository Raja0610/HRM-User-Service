package com.hrm.project.user_service.dto;

import java.time.LocalDateTime;

/**
 * Standard error response DTO returned by global exception handler.
 *
 * @param timestamp time when the exception occurred
 * @param status    HTTP status code
 * @param message   exception message
 * @param path      API endpoint path
 */
public record ErrorDetailsDto(
        LocalDateTime timestamp,
        int status,
        String message,
        String path) {
}