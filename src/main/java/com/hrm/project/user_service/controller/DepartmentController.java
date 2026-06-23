package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.DepartmentDto;
import com.hrm.project.user_service.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller responsible for managing Department related operations.
 *
 * <p>
 * Provides APIs for:
 * <ul>
 * <li>Creating a department under a branch</li>
 * <li>Fetching departments with filtering and pagination support</li>
 * <li>Updating department details</li>
 * <li>Deleting a department</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/branches/{branchId}/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Constructor-based dependency injection.
     *
     * @param departmentService service responsible for department operations
     */
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * Creates a new department under the specified branch.
     *
     * @param branchId      branch identifier
     * @param departmentDto department details
     * @return created department
     */
    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@PathVariable("branchId") UUID branchId,
                                                          @Valid @RequestBody DepartmentDto departmentDto) {

        DepartmentDto response = departmentService.createDepartment(branchId, departmentDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Fetches department(s) belonging to the specified branch.
     *
     * <p>
     * Supports:
     * <ul>
     *     <li>Fetch department by ID</li>
     *     <li>Fetch department by name</li>
     *     <li>Paginated department listing</li>
     *     <li>Sorting support</li>
     * </ul>
     * </p>
     *
     * @param branchId   branch identifier
     * @param id         optional department identifier
     * @param name       optional department name
     * @param pageNumber page number
     * @param pageSize   page size
     * @param sortBy     sorting field
     * @return filtered department data
     */
    @GetMapping
    public ResponseEntity<?> getDepartments(@PathVariable("branchId") UUID branchId,
                                            @RequestParam(required = false) UUID id,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(defaultValue = "0") int pageNumber,
                                            @RequestParam(required = false) Integer pageSize,
                                            @RequestParam(defaultValue = "name") String sortBy) {

        return ResponseEntity.ok(departmentService.getAllDepartments(branchId,
                        id,
                        name,
                        pageNumber,
                        pageSize,
                        sortBy
                )
        );
    }

    /**
     * Updates an existing department.
     *
     * @param branchId      branch identifier
     * @param id            department identifier
     * @param departmentDto updated department details
     * @return updated department information
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable("branchId") UUID branchId,
                                                          @PathVariable("id") UUID id,
                                                          @Valid @RequestBody DepartmentDto departmentDto) {

        DepartmentDto response = departmentService.updateDepartment(branchId,
                id,
                departmentDto
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a department belonging to the specified branch.
     *
     * @param branchId branch identifier
     * @param id       department identifier
     * @return success message indicating deletion completion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable("branchId") UUID branchId,
                                                   @PathVariable("id") UUID id) {

        departmentService.deleteDepartment(branchId, id);

        return ResponseEntity.ok("Department deleted successfully");
    }

}
