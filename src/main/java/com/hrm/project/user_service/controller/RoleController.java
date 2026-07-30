package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.RoleDto;
import com.hrm.project.user_service.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST endpoints for organization roles.
 */
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@PathVariable UUID organizationId,
                                              @Valid @RequestBody RoleDto roleDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(organizationId, roleDto));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoles(@PathVariable UUID organizationId,
                                                        @RequestParam(required = false) UUID id,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String displayName,
                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(required = false) Integer pageSize,
                                                        @RequestParam(defaultValue = "name") String sortBy) {
        return ResponseEntity.ok(roleService.getRoles(organizationId, id, name, displayName, pageNumber, pageSize, sortBy));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable UUID organizationId,
                                              @PathVariable UUID roleId,
                                              @Valid @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.updateRole(organizationId, roleId, roleDto));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable UUID organizationId, @PathVariable UUID roleId) {
        roleService.deleteRole(organizationId, roleId);
        return new ResponseEntity<>("Role is deleted successfully", HttpStatus.OK);
    }

}
