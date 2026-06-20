package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.OrganizationDto;
import com.hrm.project.user_service.dto.OrganizationUpdateDto;
import com.hrm.project.user_service.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller responsible for managing Organization related operations.
 * <p>
 * Provides APIs for:
 * <ul>
 *     <li>Creating an organization</li>
 *     <li>Fetching organizations with filtering and pagination support</li>
 *     <li>Updating organization details</li>
 *     <li>Deleting an organization</li>
 * </ul>
 */
@RestController
@RequestMapping("api/v1/organizations")
@CrossOrigin(origins = "*")
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * Constructor-based dependency injection.
     *
     * @param organizationService service responsible for organization operations
     */
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Creates a new organization.
     *
     * @param organizationDto organization details to be created
     * @return created organization details
     */
    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(@Valid @RequestBody OrganizationDto organizationDto) {

        OrganizationDto response = organizationService.createOrganization(organizationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Fetches organization(s) based on the provided filters.
     * <p>
     * Supports:
     * <ul>
     *     <li>Fetch organization by ID</li>
     *     <li>Fetch organization by short name</li>
     *     <li>Paginated organization listing</li>
     *     <li>Sorting support</li>
     * </ul>
     *
     * @param id         optional organization identifier
     * @param shortName  optional organization short name
     * @param pageNumber page number for pagination
     * @param pageSize   number of records per page
     * @param sortBy     field used for sorting
     * @return filtered organization data
     */
    @GetMapping
    public ResponseEntity<?> getOrganizations(@RequestParam(required = false) UUID id,
                                              @RequestParam(required = false) String shortName,
                                              @RequestParam(defaultValue = "0") int pageNumber,
                                              @RequestParam(required = false) Integer pageSize,
                                              @RequestParam(defaultValue = "asc") String sortBy) {

        return ResponseEntity.ok(organizationService.getAllOrganizations(id,
                shortName,
                pageNumber,
                pageSize,
                sortBy));
    }

    /**
     * Updates an existing organization.
     *
     * @param id                    organization identifier
     * @param organizationUpdateDto updated organization details
     * @return updated organization information
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDto> updateOrganization(@PathVariable("id") UUID id,
                                                              @Valid @RequestBody OrganizationUpdateDto organizationUpdateDto) {

        OrganizationDto response = organizationService.updateOrganization(id, organizationUpdateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an organization by its identifier.
     *
     * @param id organization identifier
     * @return success message indicating deletion completion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrganization(@PathVariable("id") UUID id) {

        organizationService.deleteOrganization(id);
        return ResponseEntity.ok("Organization deleted successfully");
    }
}