package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.OrganizationUserDto;
import com.hrm.project.user_service.dto.UserDto;
import com.hrm.project.user_service.service.OrganizationUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing users within an organization.
 * Provides endpoints for assigning, updating, retrieving,
 * and removing organization users.
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/users")
@RequiredArgsConstructor
public class OrganizationUserController {

    private final OrganizationUserService organizationUserService;

    /**
     * Assigns a user to the specified organization.
     *
     * @param organizationId the organization identifier
     * @param userDto the user details
     * @return the created organization-user association
     */
    @PostMapping
    public ResponseEntity<OrganizationUserDto> assignUserToOrganization(@PathVariable("organizationId") UUID organizationId,
                                                                        @Valid @RequestBody UserDto userDto) {

        log.info("Assigning user with email '{}' to organization '{}'.", userDto.email(), organizationId);

        OrganizationUserDto response = organizationUserService.assignUserToOrganization(organizationId, userDto);

        log.info("User successfully assigned to organization '{}'.", organizationId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing user within the specified organization.
     *
     * @param organizationId the organization identifier
     * @param userId the user identifier
     * @param organizationUserDto the updated user details
     * @return the updated organization-user association
     */
    @PutMapping("/{userId}")
    public ResponseEntity<OrganizationUserDto> updateOrganizationUser(@PathVariable("organizationId") UUID organizationId,
                                                                      @PathVariable("userId") UUID userId,
                                                                      @Valid @RequestBody UserDto organizationUserDto) {

        log.info("Updating user '{}' in organization '{}'.", userId, organizationId);

        OrganizationUserDto response = organizationUserService.updateOrganizationUser(
                organizationId,
                userId,
                organizationUserDto
        );

        log.info("User '{}' updated successfully in organization '{}'.", userId, organizationId);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves users belonging to the specified organization with optional filters.
     *
     * @param organizationId the organization identifier
     * @param userName optional user name filter
     * @param email optional email filter
     * @param branchId optional branch identifier
     * @param departmentId optional department identifier
     * @param pageNumber page number
     * @param pageSize page size
     * @return paginated list of organization users
     */
    @GetMapping
    public ResponseEntity<Object> getOrganizationUsers(@PathVariable("organizationId") UUID organizationId,
                                                       @RequestParam(required = false) String userName,
                                                       @RequestParam(required = false) String email,
                                                       @RequestParam(required = false) UUID branchId,
                                                       @RequestParam(required = false) UUID departmentId,
                                                       @RequestParam(defaultValue = "0") int pageNumber,
                                                       @RequestParam(required = false) Integer pageSize) {

        log.info("Fetching users for organization '{}' with filters: userName='{}', email='{}', branchId='{}', departmentId='{}', page={}, pageSize={}.",
                organizationId, userName, email, branchId, departmentId, pageNumber, pageSize);

        return ResponseEntity.ok(
                organizationUserService.getOrganizationUsers(
                        organizationId,
                        userName,
                        email,
                        branchId,
                        departmentId,
                        pageNumber,
                        pageSize
                )
        );
    }

    /**
     * Removes a user from the specified organization.
     *
     * @param organizationId the organization identifier
     * @param userId the user identifier
     * @return success message
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeUserFromOrganization(@PathVariable("organizationId") UUID organizationId,
                                                             @PathVariable("userId") UUID userId) {

        log.info("Removing user '{}' from organization '{}'.", userId, organizationId);

        organizationUserService.removeUserFromOrganization(organizationId, userId);

        log.info("User '{}' removed successfully from organization '{}'.", userId, organizationId);

        return ResponseEntity.ok("User removed from organization successfully");
    }
}