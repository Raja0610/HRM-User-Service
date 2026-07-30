package com.hrm.project.user_service.assembler;

import com.hrm.project.user_service.dto.RoleDto;
import com.hrm.project.user_service.entity.Role;
import org.springframework.stereotype.Component;

/** Converts role entities to and from their API representation. */
@Component
public class RoleAssembler implements BaseAssembler<Role, RoleDto> {

    @Override
    public RoleDto toDto(Role role) {
        if (role == null) {
            return null;
        }

        return new RoleDto(
                role.getId(),
                role.getName(),
                role.getDisplayName(),
                role.getDescription(),
                role.getOrganization() == null ? null : role.getOrganization().getId()
        );
    }

    @Override
    public Role toEntity(RoleDto roleDto) {
        if (roleDto == null) {
            return null;
        }

        Role role = new Role();
        role.setName(roleDto.name());
        role.setDisplayName(roleDto.displayName());
        role.setDescription(roleDto.description());
        return role;
    }
}
