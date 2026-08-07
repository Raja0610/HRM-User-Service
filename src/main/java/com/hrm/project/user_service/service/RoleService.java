package com.hrm.project.user_service.service;

import com.hrm.project.user_service.dto.RoleDto;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

public interface RoleService {

    RoleDto createRole(UUID organizationId, @Valid RoleDto roleDto);

    Map<String, Object> getRoles(UUID organizationId, UUID id, String name, String displayName,
                                 int pageNumber, Integer pageSize, String sortBy);

    RoleDto updateRole(UUID organizationId, UUID roleId, @Valid RoleDto roleDto);

    void deleteRole(UUID organizationId, UUID roleId);

}
