package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.OrganizationUserAssembler;
import com.hrm.project.user_service.dto.OrganizationUserDto;
import com.hrm.project.user_service.dto.UserDto;
import com.hrm.project.user_service.entity.Organization;
import com.hrm.project.user_service.entity.OrganizationUser;
import com.hrm.project.user_service.entity.OrganizationUserId;
import com.hrm.project.user_service.entity.User;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.OrganizationRepository;
import com.hrm.project.user_service.repository.OrganizationUserRepository;
import com.hrm.project.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationUserServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @Mock
    private OrganizationUserAssembler organizationUserAssembler;

    @InjectMocks
    private OrganizationUserServiceImpl organizationUserService;

    @Test
    void assignUserToOrganization_ShouldThrowException_WhenOrganizationNotFound() {

        UUID organizationId = UUID.randomUUID();

        UserDto userDto = new UserDto(
                null,
                "John",
                "Doe",
                "9876543210",
                "john@test.com",
                null,
                null,
                null,
                null
        );

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> organizationUserService.assignUserToOrganization(organizationId, userDto)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void assignUserToOrganization_ShouldAssignNewUserSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization organization = new Organization();

        User savedUser = new User();
        savedUser.setId(userId);

        UserDto userDto = new UserDto(
                null,
                "John",
                "Doe",
                "9876543210",
                "john@test.com",
                null,
                null,
                null,
                null
        );

        OrganizationUserDto responseDto = mock(OrganizationUserDto.class);

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(userRepository.findByEmail(userDto.email()))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(organizationUserAssembler.toDto(any(OrganizationUser.class)))
                .thenReturn(responseDto);

        OrganizationUserDto result =
                organizationUserService.assignUserToOrganization(organizationId, userDto);

        assertNotNull(result);

        verify(userRepository).save(any(User.class));
        verify(organizationUserRepository).save(any(OrganizationUser.class));
    }

    @Test
    void assignUserToOrganization_ShouldThrowException_WhenAlreadyAssigned() {

        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization organization = new Organization();

        User existingUser = new User();
        existingUser.setId(userId);

        UserDto userDto = new UserDto(
                null,
                "John",
                "Doe",
                "9876543210",
                "john@test.com",
                null,
                null,
                null,
                null
        );

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(userRepository.findByEmail(userDto.email()))
                .thenReturn(Optional.of(existingUser));

        when(organizationUserRepository.existsById(any(OrganizationUserId.class)))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> organizationUserService.assignUserToOrganization(organizationId, userDto)
        );

        verify(organizationUserRepository, never()).save(any());
    }

    @Test
    void updateOrganizationUser_ShouldThrowException_WhenOrganizationNotFound() {

        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> organizationUserService.updateOrganizationUser(
                        organizationId,
                        userId,
                        mock(UserDto.class)
                )
        );
    }

    @Test
    void updateOrganizationUser_ShouldUpdateSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization organization = new Organization();

        User user = new User();

        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setUser(user);

        UserDto userDto = new UserDto(
                userId,
                "Updated",
                "User",
                "9999999999",
                "updated@test.com",
                null,
                null,
                null,
                null
        );

        OrganizationUserDto responseDto = mock(OrganizationUserDto.class);

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(
                organizationUserRepository.findByOrganizationIdAndUserId(
                        organizationId,
                        userId
                )
        ).thenReturn(Optional.of(organizationUser));

        when(organizationUserAssembler.toDto(organizationUser))
                .thenReturn(responseDto);

        OrganizationUserDto result =
                organizationUserService.updateOrganizationUser(
                        organizationId,
                        userId,
                        userDto
                );

        assertNotNull(result);

        verify(userRepository).save(user);
    }

    @Test
    void getOrganizationUsers_ShouldReturnPagedResponse() {

        UUID organizationId = UUID.randomUUID();

        OrganizationUser organizationUser = new OrganizationUser();

        Page<OrganizationUser> page =
                new PageImpl<>(
                        List.of(organizationUser),
                        PageRequest.of(0, 10),
                        1
                );

        when(organizationUserRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        when(organizationUserAssembler.toDto(any()))
                .thenReturn(mock(OrganizationUserDto.class));

        Map<String, Object> result =
                organizationUserService.getOrganizationUsers(
                        organizationId,
                        null,
                        null,
                        null,
                        null,
                        0,
                        10
                );

        assertNotNull(result);
        assertTrue(result.containsKey("pager"));
        assertTrue(result.containsKey("items"));
    }

    @Test
    void removeUserFromOrganization_ShouldDeleteSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        OrganizationUser organizationUser = new OrganizationUser();

        when(organizationUserRepository.findById(any(OrganizationUserId.class)))
                .thenReturn(Optional.of(organizationUser));

        organizationUserService.removeUserFromOrganization(
                organizationId,
                userId
        );

        verify(organizationUserRepository).delete(organizationUser);
    }

    @Test
    void removeUserFromOrganization_ShouldThrowDependentResourceDeleteException() {

        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        OrganizationUser organizationUser = new OrganizationUser();

        when(organizationUserRepository.findById(any(OrganizationUserId.class)))
                .thenReturn(Optional.of(organizationUser));

        doThrow(new RuntimeException("Constraint violation"))
                .when(organizationUserRepository)
                .delete(organizationUser);

        assertThrows(
                DependentResourceDeleteException.class,
                () -> organizationUserService.removeUserFromOrganization(
                        organizationId,
                        userId
                )
        );
    }
}