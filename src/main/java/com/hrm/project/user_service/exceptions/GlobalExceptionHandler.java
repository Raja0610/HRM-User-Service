package com.hrm.project.user_service.exceptions;

import com.hrm.project.user_service.ResourceAlreadyExistsException;
import com.hrm.project.user_service.dto.ErrorDetailsDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler responsible for handling all application exceptions
 * and returning standardized error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException.
     *
     * @param exception thrown exception
     * @param request   current HTTP request
     * @return standardized 404 response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                           HttpServletRequest request) {

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    /**
     * Handles ResourceAlreadyExistsException.
     *
     * @param exception thrown exception
     * @param request   current HTTP request
     * @return standardized 409 response
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception,
                                                                                HttpServletRequest request) {

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }
}