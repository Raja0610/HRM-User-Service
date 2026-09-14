package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.RoleAssembler;
import com.hrm.project.user_service.dto.RoleDto;
import com.hrm.project.user_service.entity.Authority;
import com.hrm.project.user_service.entity.Organization;
import com.hrm.project.user_service.entity.OrganizationUser;
import com.hrm.project.user_service.entity.Role;
import com.hrm.project.user_service.exceptions.BusinessLogicException;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.AuthorityRepository;
import com.hrm.project.user_service.repository.OrganizationRepository;
import com.hrm.project.user_service.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private RoleAssembler roleAssembler;

    @InjectMocks
    private RoleServiceImpl roleService;


    // =========================================================
    // CREATE ROLE
    // =========================================================

    @Test
    void shouldCreateRoleSuccessfully() {

        UUID organizationId = UUID.randomUUID();

        RoleDto roleDto = roleDto();

        Organization organization = organization(organizationId);

        Role role = role(UUID.randomUUID(), organizationId);

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(roleRepository.existsByNameIgnoreCase(roleDto.name()))
                .thenReturn(false);

        when(roleAssembler.toEntity(roleDto))
                .thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleAssembler.toDto(role))
                .thenReturn(roleDto);

        RoleDto result =
                roleService.createRole(organizationId, roleDto);

        assertNotNull(result);
        assertEquals(roleDto, result);
        assertEquals(organization, role.getOrganization());

        verify(organizationRepository)
                .findById(organizationId);

        verify(roleRepository)
                .existsByNameIgnoreCase(roleDto.name());

        verify(roleAssembler)
                .toEntity(roleDto);

        verify(roleRepository)
                .save(role);

        verify(roleAssembler)
                .toDto(role);
    }


    @Test
    void shouldThrowExceptionWhenOrganizationDoesNotExistWhileCreatingRole() {

        UUID organizationId = UUID.randomUUID();

        RoleDto roleDto = roleDto();

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.createRole(
                        organizationId,
                        roleDto
                )
        );

        verify(organizationRepository)
                .findById(organizationId);

        verify(roleRepository, never())
                .existsByNameIgnoreCase(any());

        verify(roleAssembler, never())
                .toEntity(any());

        verify(roleRepository, never())
                .save(any());

        verify(roleAssembler, never())
                .toDto(any());
    }


    @Test
    void shouldThrowExceptionWhenRoleNameAlreadyExistsWhileCreatingRole() {

        UUID organizationId = UUID.randomUUID();

        RoleDto roleDto = roleDto();

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.existsByNameIgnoreCase(roleDto.name()))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> roleService.createRole(
                        organizationId,
                        roleDto
                )
        );

        verify(roleRepository)
                .existsByNameIgnoreCase(roleDto.name());

        verify(roleAssembler, never())
                .toEntity(any());

        verify(roleRepository, never())
                .save(any());

        verify(roleAssembler, never())
                .toDto(any());
    }


    // =========================================================
    // GET ROLES
    // =========================================================

    @Test
    void shouldGetRolesSuccessfully() {

        UUID organizationId = UUID.randomUUID();

        Role role1 = role(UUID.randomUUID(), organizationId);
        Role role2 = role(UUID.randomUUID(), organizationId);

        RoleDto dto1 = roleDto();

        RoleDto dto2 = new RoleDto(
                null,
                "USER",
                "User",
                "User role",
                null,
                null,
                null
        );

        Page<Role> page =
                new PageImpl<>(List.of(role1, role2));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        when(roleAssembler.toDto(role1))
                .thenReturn(dto1);

        when(roleAssembler.toDto(role2))
                .thenReturn(dto2);

        Map<String, Object> result =
                roleService.getRoles(
                        organizationId,
                        null,
                        null,
                        null,
                        0,
                        10,
                        "name"
                );

        assertNotNull(result);
        assertTrue(result.containsKey("pager"));
        assertTrue(result.containsKey("items"));

        List<?> items =
                (List<?>) result.get("items");

        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals(dto1, items.get(0));
        assertEquals(dto2, items.get(1));

        verify(organizationRepository)
                .findById(organizationId);

        verify(roleRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        verify(roleAssembler)
                .toDto(role1);

        verify(roleAssembler)
                .toDto(role2);
    }


    @Test
    void shouldReturnEmptyRoleListWhenNoRolesExist() {

        UUID organizationId = UUID.randomUUID();

        Page<Role> page =
                new PageImpl<>(List.of());

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        Map<String, Object> result =
                roleService.getRoles(
                        organizationId,
                        null,
                        null,
                        null,
                        0,
                        10,
                        "name"
                );

        List<?> items =
                (List<?>) result.get("items");

        assertNotNull(items);
        assertTrue(items.isEmpty());

        verify(roleAssembler, never())
                .toDto(any());
    }


    @Test
    void shouldThrowExceptionWhenOrganizationDoesNotExistWhileGettingRoles() {

        UUID organizationId = UUID.randomUUID();

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.getRoles(
                        organizationId,
                        null,
                        null,
                        null,
                        0,
                        10,
                        "name"
                )
        );

        verify(roleRepository, never())
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        verify(roleAssembler, never())
                .toDto(any());
    }


    @Test
    void shouldGetRolesUsingIdFilter() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        RoleDto dto = roleDto();

        Page<Role> page =
                new PageImpl<>(List.of(role));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        when(roleAssembler.toDto(role))
                .thenReturn(dto);

        Map<String, Object> result =
                roleService.getRoles(
                        organizationId,
                        roleId,
                        null,
                        null,
                        0,
                        10,
                        "name"
                );

        List<?> items =
                (List<?>) result.get("items");

        assertEquals(1, items.size());
        assertEquals(dto, items.getFirst());

        verify(roleRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }


    @Test
    void shouldGetRolesUsingNameFilter() {

        UUID organizationId = UUID.randomUUID();

        Role role =
                role(UUID.randomUUID(), organizationId);

        RoleDto dto = roleDto();

        Page<Role> page =
                new PageImpl<>(List.of(role));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        when(roleAssembler.toDto(role))
                .thenReturn(dto);

        Map<String, Object> result =
                roleService.getRoles(
                        organizationId,
                        null,
                        "ADMIN",
                        null,
                        0,
                        10,
                        "name"
                );

        List<?> items =
                (List<?>) result.get("items");

        assertEquals(1, items.size());
        assertEquals(dto, items.get(0));
    }


    @Test
    void shouldGetRolesUsingDisplayNameFilter() {

        UUID organizationId = UUID.randomUUID();

        Role role =
                role(UUID.randomUUID(), organizationId);

        RoleDto dto = roleDto();

        Page<Role> page =
                new PageImpl<>(List.of(role));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        when(roleAssembler.toDto(role))
                .thenReturn(dto);

        Map<String, Object> result =
                roleService.getRoles(
                        organizationId,
                        null,
                        null,
                        "Administrator",
                        0,
                        10,
                        "name"
                );

        List<?> items =
                (List<?>) result.get("items");

        assertEquals(1, items.size());
        assertEquals(dto, items.getFirst());
    }


    // =========================================================
    // PAGINATION
    // =========================================================

    @Test
    void shouldUsePagedRequestWhenPageSizeIsPositive() {

        UUID organizationId = UUID.randomUUID();

        Page<Role> page =
                new PageImpl<>(List.of());

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        roleService.getRoles(
                organizationId,
                null,
                null,
                null,
                2,
                10,
                "name"
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(roleRepository).findAll(
                any(Specification.class),
                captor.capture()
        );

        Pageable pageable =
                captor.getValue();

        assertFalse(pageable.isUnpaged());
        assertEquals(2, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }


    @Test
    void shouldUseUnpagedWhenPageSizeIsNull() {

        UUID organizationId = UUID.randomUUID();

        Page<Role> page =
                new PageImpl<>(List.of());

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        roleService.getRoles(
                organizationId,
                null,
                null,
                null,
                0,
                null,
                "name"
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(roleRepository).findAll(
                any(Specification.class),
                captor.capture()
        );

        assertEquals(
                Pageable.unpaged(),
                captor.getValue()
        );
    }


    @Test
    void shouldUseUnpagedWhenPageSizeIsZero() {

        UUID organizationId = UUID.randomUUID();

        Page<Role> page =
                new PageImpl<>(List.of());

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        roleService.getRoles(
                organizationId,
                null,
                null,
                null,
                0,
                0,
                "name"
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(roleRepository).findAll(
                any(Specification.class),
                captor.capture()
        );

        assertEquals(
                Pageable.unpaged(),
                captor.getValue()
        );
    }


    @Test
    void shouldUseUnpagedWhenPageSizeIsNegative() {

        UUID organizationId = UUID.randomUUID();

        Page<Role> page =
                new PageImpl<>(List.of());

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization(organizationId)));

        when(roleRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        roleService.getRoles(
                organizationId,
                null,
                null,
                null,
                0,
                -1,
                "name"
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(roleRepository).findAll(
                any(Specification.class),
                captor.capture()
        );

        assertEquals(
                Pageable.unpaged(),
                captor.getValue()
        );
    }


    // =========================================================
    // UPDATE ROLE
    // =========================================================

    @Test
    void shouldUpdateRoleSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        RoleDto roleDto =
                new RoleDto(null,
                        "MANAGER",
                        "Manager",
                        "Updated description",
                        null,
                        null,
                        null
                );

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        when(roleRepository.existsByNameIgnoreCaseAndIdNot(
                roleDto.name(),
                roleId
        )).thenReturn(false);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleAssembler.toDto(role))
                .thenReturn(roleDto);

        RoleDto result =
                roleService.updateRole(
                        organizationId,
                        roleId,
                        roleDto
                );

        assertNotNull(result);
        assertEquals(roleDto, result);

        assertEquals(
                "MANAGER",
                role.getName()
        );

        assertEquals(
                "Manager",
                role.getDisplayName()
        );

        assertEquals(
                "Updated description",
                role.getDescription()
        );

        verify(roleRepository)
                .findByIdAndOrganizationId(
                        roleId,
                        organizationId
                );

        verify(roleRepository)
                .existsByNameIgnoreCaseAndIdNot(
                        roleDto.name(),
                        roleId
                );

        verify(roleRepository)
                .save(role);

        verify(roleAssembler)
                .toDto(role);
    }


    @Test
    void shouldThrowExceptionWhenRoleDoesNotExistWhileUpdating() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        RoleDto roleDto = roleDto();

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.updateRole(
                        organizationId,
                        roleId,
                        roleDto
                )
        );

        verify(roleRepository, never())
                .existsByNameIgnoreCaseAndIdNot(
                        any(),
                        any()
                );

        verify(roleRepository, never())
                .save(any());

        verify(roleAssembler, never())
                .toDto(any());
    }


    @Test
    void shouldThrowExceptionWhenUpdatedRoleNameAlreadyExists() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        RoleDto roleDto =
                new RoleDto(null,
                        "ADMIN",
                        "Administrator",
                        "Description",
                        null,
                        null,
                        null
                );

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        when(roleRepository.existsByNameIgnoreCaseAndIdNot(
                roleDto.name(),
                roleId
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> roleService.updateRole(
                        organizationId,
                        roleId,
                        roleDto
                )
        );

        verify(roleRepository)
                .existsByNameIgnoreCaseAndIdNot(
                        roleDto.name(),
                        roleId
                );

        verify(roleRepository, never())
                .save(any());

        verify(roleAssembler, never())
                .toDto(any());
    }


    // =========================================================
    // UPDATE ROLE AUTHORITIES
    // =========================================================

    @Test
    void shouldReplaceRoleAuthoritiesWithRequestedAuthorities() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        UUID retainedAuthorityId =
                UUID.randomUUID();

        UUID addedAuthorityId =
                UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        Authority oldAuthority =
                authority(UUID.randomUUID());

        Authority retainedAuthority =
                authority(retainedAuthorityId);

        Authority addedAuthority =
                authority(addedAuthorityId);

        role.getAuthorities()
                .add(oldAuthority);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        when(authorityRepository.findAllById(any()))
                .thenReturn(
                        List.of(
                                retainedAuthority,
                                addedAuthority
                        )
                );

        roleService.updateRoleAuthorities(
                organizationId,
                roleId,
                List.of(
                        retainedAuthorityId,
                        addedAuthorityId
                )
        );

        assertEquals(
                2,
                role.getAuthorities().size()
        );

        assertTrue(
                role.getAuthorities()
                        .contains(retainedAuthority)
        );

        assertTrue(
                role.getAuthorities()
                        .contains(addedAuthority)
        );

        assertFalse(
                role.getAuthorities()
                        .contains(oldAuthority)
        );

        verify(roleRepository)
                .findByIdAndOrganizationId(
                        roleId,
                        organizationId
                );

        verify(authorityRepository)
                .findAllById(any());
    }


    @Test
    void shouldThrowExceptionWhenRoleDoesNotExistWhileUpdatingAuthorities() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.updateRoleAuthorities(
                        organizationId,
                        roleId,
                        List.of(UUID.randomUUID())
                )
        );

        verify(authorityRepository, never())
                .findAllById(any());
    }


    @Test
    void shouldRejectUnknownAuthority() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        UUID missingAuthorityId = UUID.randomUUID();

        Role role = role(roleId, organizationId);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        when(authorityRepository.findAllById(any()))
                .thenReturn(List.of());

        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> roleService.updateRoleAuthorities(
                        organizationId,
                        roleId,
                        List.of(missingAuthorityId)
                )
        );

        assertEquals(
                "Some requested authorities were not found",
                exception.getMessage()
        );

        assertTrue(role.getAuthorities().isEmpty());

        verify(authorityRepository)
                .findAllById(any());

        verify(roleAssembler, never())
                .toDto(any());
    }

    @Test
    void shouldRejectWhenOneOfMultipleAuthoritiesDoesNotExist() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        UUID existingAuthorityId = UUID.randomUUID();
        UUID missingAuthorityId = UUID.randomUUID();

        Role role = role(roleId, organizationId);

        Authority existingAuthority =
                authority(existingAuthorityId);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        when(authorityRepository.findAllById(any()))
                .thenReturn(List.of(existingAuthority));

        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> roleService.updateRoleAuthorities(
                        organizationId,
                        roleId,
                        List.of(
                                existingAuthorityId,
                                missingAuthorityId
                        )
                )
        );

        assertEquals(
                "Some requested authorities were not found",
                exception.getMessage()
        );

        // Authorities must not be partially updated
        assertTrue(role.getAuthorities().isEmpty());

        verify(authorityRepository)
                .findAllById(any());

        verify(roleAssembler, never())
                .toDto(any());
    }


    @Test
    void shouldHandleDuplicateAuthorityIds() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        UUID authorityId =
                UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        Authority authority =
                authority(authorityId);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        when(authorityRepository.findAllById(any()))
                .thenReturn(List.of(authority));

        roleService.updateRoleAuthorities(
                organizationId,
                roleId,
                List.of(
                        authorityId,
                        authorityId
                )
        );

        assertEquals(
                1,
                role.getAuthorities().size()
        );

        assertTrue(
                role.getAuthorities()
                        .contains(authority)
        );

        ArgumentCaptor<Iterable<UUID>> captor =
                ArgumentCaptor.forClass(Iterable.class);

        verify(authorityRepository)
                .findAllById(captor.capture());

        List<UUID> requestedIds =
                toList(captor.getValue());

        assertEquals(
                1,
                requestedIds.size()
        );

        assertEquals(
                authorityId,
                requestedIds.get(0)
        );
    }


    @Test
    void shouldClearAuthoritiesWhenEmptyListIsProvided() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        Authority existingAuthority =
                authority(UUID.randomUUID());

        role.getAuthorities()
                .add(existingAuthority);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        when(authorityRepository.findAllById(any()))
                .thenReturn(List.of());

        roleService.updateRoleAuthorities(
                organizationId,
                roleId,
                List.of()
        );

        assertTrue(
                role.getAuthorities().isEmpty()
        );

        verify(roleRepository)
                .findByIdAndOrganizationId(
                        roleId,
                        organizationId
                );
    }


    // =========================================================
    // DELETE ROLE
    // =========================================================

    @Test
    void shouldDeleteRoleSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        OrganizationUser organizationUser =
                new OrganizationUser();

        organizationUser.getRoles()
                .add(role);

        role.getOrganizationUsers()
                .add(organizationUser);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        roleService.deleteRole(
                organizationId,
                roleId
        );

        assertFalse(
                organizationUser.getRoles()
                        .contains(role)
        );

        verify(roleRepository)
                .findByIdAndOrganizationId(
                        roleId,
                        organizationId
                );

        verify(roleRepository)
                .delete(role);

        verify(roleRepository)
                .flush();
    }


    @Test
    void shouldDeleteRoleSuccessfullyWhenRoleHasNoOrganizationUsers() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        roleService.deleteRole(
                organizationId,
                roleId
        );

        verify(roleRepository)
                .delete(role);

        verify(roleRepository)
                .flush();
    }


    @Test
    void shouldThrowExceptionWhenRoleDoesNotExistWhileDeleting() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.deleteRole(
                        organizationId,
                        roleId
                )
        );

        verify(roleRepository, never())
                .delete((Role) any());

        verify(roleRepository, never())
                .flush();
    }


    @Test
    void shouldThrowDependentResourceDeleteExceptionWhenDeleteFails() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        doThrow(
                new RuntimeException("Delete failed")
        ).when(roleRepository).delete(role);

        assertThrows(
                DependentResourceDeleteException.class,
                () -> roleService.deleteRole(
                        organizationId,
                        roleId
                )
        );

        verify(roleRepository)
                .delete(role);

        verify(roleRepository, never())
                .flush();
    }


    @Test
    void shouldThrowDependentResourceDeleteExceptionWhenFlushFails() {

        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Role role =
                role(roleId, organizationId);

        when(roleRepository.findByIdAndOrganizationId(
                roleId,
                organizationId
        )).thenReturn(Optional.of(role));

        doThrow(
                new RuntimeException("Flush failed")
        ).when(roleRepository).flush();

        assertThrows(
                DependentResourceDeleteException.class,
                () -> roleService.deleteRole(
                        organizationId,
                        roleId
                )
        );

        verify(roleRepository)
                .delete(role);

        verify(roleRepository)
                .flush();
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private RoleDto roleDto() {

        return new RoleDto(null,
                "ADMIN",
                "Administrator",
                "Administrator role",
                null,
                null,
                null
        );
    }


    private Organization organization(
            UUID organizationId
    ) {

        Organization organization =
                new Organization();

        organization.setId(organizationId);

        return organization;
    }


    private Role role(
            UUID roleId,
            UUID organizationId
    ) {

        Role role =
                new Role();

        role.setId(roleId);

        role.setOrganization(
                organization(organizationId)
        );

        return role;
    }


    private Authority authority(
            UUID authorityId
    ) {

        Authority authority =
                new Authority();

        authority.setId(authorityId);

        return authority;
    }


    private List<UUID> toList(
            Iterable<UUID> iterable
    ) {

        List<UUID> result =
                new ArrayList<>();

        iterable.forEach(result::add);

        return result;
    }
}