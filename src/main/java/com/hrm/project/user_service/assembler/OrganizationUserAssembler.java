package com.hrm.project.user_service.assembler;

import com.hrm.project.user_service.dto.OrganizationUserDto;
import com.hrm.project.user_service.dto.UserDto;
import com.hrm.project.user_service.entity.OrganizationUser;
import org.springframework.stereotype.Component;

/**
 * Assembler responsible for converting {@link OrganizationUser} entities
 * into {@link OrganizationUserDto} objects.
 */
@Component
public class OrganizationUserAssembler implements BaseAssembler<OrganizationUser, OrganizationUserDto> {

    /**
     * Converts an {@link OrganizationUser} entity into an {@link OrganizationUserDto}.
     * It also maps the associated user information into a {@link UserDto}.
     *
     * @param organizationUser the organization-user association entity
     * @return the corresponding OrganizationUserDto
     */
    @Override
    public OrganizationUserDto toDto(OrganizationUser organizationUser) {

        // Map the associated user entity to UserDto.
        UserDto userDto = new UserDto(
                organizationUser.getUser().getId(),
                organizationUser.getUser().getFirstName(),
                organizationUser.getUser().getLastName(),
                organizationUser.getUser().getMobileNumber(),
                organizationUser.getUser().getEmail(),
                organizationUser.getOrganization().getId(),
                organizationUser.getOrganization().getName(),
                organizationUser.getBranch() != null
                        ? organizationUser.getBranch().getId()
                        : null,
                organizationUser.getDepartment() != null
                        ? organizationUser.getDepartment().getId()
                        : null
        );

        // Build and return the OrganizationUserDto.
        return OrganizationUserDto.builder()
                .organizationId(
                        organizationUser.getOrganization().getId()
                )
                .organizationName(
                        organizationUser.getOrganization().getName()
                )
                .user(userDto)
                .build();
    }

    /**
     * Conversion from DTO to entity is intentionally not supported because
     * OrganizationUser creation involves business logic handled by the service layer.
     *
     * @param organizationUserDto the DTO to convert
     * @return never returns normally
     * @throws UnsupportedOperationException always thrown
     */
    @Override
    public OrganizationUser toEntity(OrganizationUserDto organizationUserDto) {
        throw new UnsupportedOperationException(
                "OrganizationUser entity creation should be handled in service layer"
        );
    }
}