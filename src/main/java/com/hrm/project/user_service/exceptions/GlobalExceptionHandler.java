package com.hrm.project.user_service.exceptions;

import com.hrm.project.user_service.dto.ErrorDetailsDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler responsible for handling all application exceptions
 * and returning standardized error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

        log.warn("Resource not found for {}: {}", request.getRequestURI(), exception.getMessage());

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

        log.warn("Resource conflict for {}: {}", request.getRequestURI(), exception.getMessage());

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    /**
     * Handles DependentResourceDeleteException.
     *
     * @param exception thrown exception
     * @param request   current HTTP request
     * @return standardized 424 response
     */
    @ExceptionHandler(DependentResourceDeleteException.class)
    public ResponseEntity<ErrorDetailsDto> handleDependentResourceDeleteException(DependentResourceDeleteException exception,
                                                                                  HttpServletRequest request) {

        log.warn("DependentResourceDelete exception for {}: {}", request.getRequestURI(), exception.getMessage());

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                HttpStatus.FAILED_DEPENDENCY.value(),
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(errorDetails);
    }

    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErrorDetailsDto> handleValidationException(Exception exception,
                                                                     HttpServletRequest request) {
        log.warn("Validation failed for {}: {}", request.getRequestURI(), exception.getMessage());

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.badRequest().body(errorDetails);
    }
}
