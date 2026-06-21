package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.BranchDto;
import com.hrm.project.user_service.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller responsible for managing Branch related operations.
 * <p>
 * Provides APIs for:
 * <ul>
 *     <li>Creating a branch under an organization</li>
 *     <li>Fetching branches with filtering and pagination support</li>
 *     <li>Updating branch details</li>
 *     <li>Deleting a branch</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/branches")
@CrossOrigin(origins = "*")
public class BranchController {

    private final BranchService branchService;

    /**
     * Constructor-based dependency injection.
     *
     * @param branchService service responsible for branch operations
     */
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    /**
     * Creates a new branch under the specified organization.
     *
     * @param organizationId organization identifier
     * @param branchDto      branch details
     * @return created branch
     */
    @PostMapping
    public ResponseEntity<BranchDto> createBranch(@PathVariable("organizationId") UUID organizationId,
                                                  @Valid @RequestBody BranchDto branchDto) {

        BranchDto response = branchService.createBranch(organizationId, branchDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Fetches branch(es) belonging to the specified organization.
     * <p>
     * Supports:
     * <ul>
     *     <li>Fetch branch by ID</li>
     *     <li>Fetch branch by name</li>
     *     <li>Paginated branch listing</li>
     *     <li>Sorting support</li>
     * </ul>
     *
     * @param organizationId organization identifier
     * @param id             optional branch identifier
     * @param name           optional branch name
     * @param pageNumber     page number
     * @param pageSize       page size
     * @param sortBy         sorting field
     * @return filtered branch data
     */
    @GetMapping
    public ResponseEntity<?> getBranches(@PathVariable("organizationId") UUID organizationId,
                                         @RequestParam(required = false) UUID id,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(defaultValue = "0") int pageNumber,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(defaultValue = "name") String sortBy) {

        return ResponseEntity.ok(branchService.getAllBranches(organizationId,
                id,
                name,
                pageNumber,
                pageSize,
                sortBy));
    }

    /**
     * Updates an existing branch.
     *
     * @param organizationId organization identifier
     * @param id             branch identifier
     * @param branchDto      updated branch details
     * @return updated branch information
     */
    @PutMapping("/{id}")
    public ResponseEntity<BranchDto> updateBranch(@PathVariable("organizationId") UUID organizationId,
                                                  @PathVariable("id") UUID id,
                                                  @Valid @RequestBody BranchDto branchDto) {

        BranchDto response = branchService.updateBranch(organizationId,
                id,
                branchDto);

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a branch belonging to the specified organization.
     *
     * @param organizationId organization identifier
     * @param id             branch identifier
     * @return success message indicating deletion completion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable("organizationId") UUID organizationId,
                                               @PathVariable("id") UUID id) {

        branchService.deleteBranch(organizationId, id);
        return ResponseEntity.ok("Branch deleted successfully");
    }
}