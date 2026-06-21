package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.dto.OrganizationDto;
import com.hrm.project.user_service.dto.OrganizationUpdateDto;
import com.hrm.project.user_service.dto.PagerDto;
import com.hrm.project.user_service.entity.Organization;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @Test
    void shouldCreateOrganizationSuccessfully() {

        UUID organizationId = UUID.randomUUID();

        OrganizationDto request = OrganizationDto.builder()
                .name("Panex")
                .displayName("Panex Logistics")
                .website("https://panex.com")
                .headQuarter("Delhi")
                .active(true)
                .establishedYear(LocalDate.of(2020, 1, 1))
                .totalWorkForce(100L)
                .build();

        Organization savedOrganization = Organization.builder()
                .id(organizationId)
                .name("Panex")
                .displayName("Panex Logistics")
                .website("https://panex.com")
                .headQuarter("Delhi")
                .active(true)
                .establishedYear(LocalDate.of(2020, 1, 1))
                .totalWorkForce(100L)
                .build();

        OrganizationDto response = OrganizationDto.builder()
                .id(organizationId)
                .name("Panex")
                .displayName("Panex Logistics")
                .website("https://panex.com")
                .headQuarter("Delhi")
                .active(true)
                .establishedYear(LocalDate.of(2020, 1, 1))
                .totalWorkForce(100L)
                .build();

        when(organizationRepository.existsByNameIgnoreCase("Panex")).thenReturn(false);
        when(organizationRepository.save(any(Organization.class))).thenReturn(savedOrganization);
        when(modelMapper.map(savedOrganization, OrganizationDto.class)).thenReturn(response);

        OrganizationDto result = organizationService.createOrganization(request);

        assertNotNull(result);
        assertEquals("Panex", result.getName());

        verify(organizationRepository).save(any(Organization.class));
    }

    @Test
    void shouldReturnOrganizationByIdFilter() {

        UUID organizationId = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(organizationId)
                .name("Panex")
                .displayName("Panex Logistics")
                .build();

        OrganizationDto organizationDto = OrganizationDto.builder()
                .id(organizationId)
                .name("Panex")
                .displayName("Panex Logistics")
                .build();

        Page<Organization> page = new PageImpl<>(List.of(organization));

        when(organizationRepository.findAll(
                ArgumentMatchers.<Specification<Organization>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        when(modelMapper.map(organization, OrganizationDto.class))
                .thenReturn(organizationDto);

        Map<String, Object> response = organizationService.getAllOrganizations(
                organizationId,
                null,
                0,
                10,
                "name"
        );

        @SuppressWarnings("unchecked")
        List<OrganizationDto> items =
                (List<OrganizationDto>) response.get("items");

        PagerDto pager = (PagerDto) response.get("pager");

        assertEquals(1, items.size());
        assertEquals(organizationId, items.getFirst().getId());

        assertEquals(1, pager.totalElements());
        assertEquals(1, pager.totalPages());

        verify(organizationRepository, times(1))
                .findAll(
                        ArgumentMatchers.<Specification<Organization>>any(),
                        any(Pageable.class)
                );
    }

    @Test
    void shouldReturnOrganizationsWithoutPaginationWhenPageSizeIsNull() {

        Organization organization = Organization.builder()
                .id(UUID.randomUUID())
                .name("Panex")
                .displayName("Panex Logistics")
                .build();

        OrganizationDto organizationDto = OrganizationDto.builder()
                .id(organization.getId())
                .name("Panex")
                .displayName("Panex Logistics")
                .build();

        Page<Organization> page = new PageImpl<>(List.of(organization));

        when(organizationRepository.findAll(
                ArgumentMatchers.<Specification<Organization>>any(),
                eq(Pageable.unpaged())
        )).thenReturn(page);

        when(modelMapper.map(organization, OrganizationDto.class))
                .thenReturn(organizationDto);

        Map<String, Object> response = organizationService.getAllOrganizations(
                null,
                null,
                0,
                null,
                "name"
        );

        @SuppressWarnings("unchecked")
        List<OrganizationDto> items =
                (List<OrganizationDto>) response.get("items");

        PagerDto pager = (PagerDto) response.get("pager");

        assertEquals(1, items.size());
        assertEquals("Panex", items.getFirst().getName());

        assertEquals(1, pager.totalElements());
        assertEquals(1, pager.totalPages());

        verify(organizationRepository, times(1))
                .findAll(
                        ArgumentMatchers.<Specification<Organization>>any(),
                        eq(Pageable.unpaged())
                );
    }

    @Test
    void shouldReturnOrganizationsByNameFilter() {

        String organizationName = "Panex";

        Organization organization = Organization.builder()
                .id(UUID.randomUUID())
                .name(organizationName)
                .displayName("Panex Logistics")
                .build();

        OrganizationDto organizationDto = OrganizationDto.builder()
                .id(organization.getId())
                .name(organizationName)
                .displayName("Panex Logistics")
                .build();

        Page<Organization> page = new PageImpl<>(List.of(organization));

        when(organizationRepository.findAll(
                ArgumentMatchers.<Specification<Organization>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        when(modelMapper.map(organization, OrganizationDto.class))
                .thenReturn(organizationDto);

        Map<String, Object> response = organizationService.getAllOrganizations(
                null,
                organizationName,
                0,
                10,
                "name"
        );

        assertNotNull(response);

        @SuppressWarnings("unchecked")
        List<OrganizationDto> items =
                (List<OrganizationDto>) response.get("items");

        PagerDto pager = (PagerDto) response.get("pager");

        assertEquals(1, items.size());
        assertEquals(organizationName, items.getFirst().getName());

        assertEquals(1, pager.totalElements());
        assertEquals(1, pager.totalPages());

        verify(organizationRepository, times(1))
                .findAll(
                        ArgumentMatchers.<Specification<Organization>>any(),
                        any(Pageable.class)
                );
    }

    @Test
    void shouldThrowExceptionWhenOrganizationAlreadyExists() {

        OrganizationDto request = OrganizationDto.builder()
                .name("Panex")
                .build();

        when(organizationRepository.existsByNameIgnoreCase("Panex")).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> organizationService.createOrganization(request)
        );

        verify(organizationRepository, never()).save(any());
    }

    @Test
    void shouldReturnOrganizationsSuccessfully() {

        Organization organization = Organization.builder()
                .id(UUID.randomUUID())
                .name("Panex")
                .build();

        OrganizationDto dto = OrganizationDto.builder()
                .name("Panex")
                .build();

        Page<Organization> page = new PageImpl<>(List.of(organization));

        when(organizationRepository.findAll(
                ArgumentMatchers.<Specification<Organization>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        when(modelMapper.map(organization, OrganizationDto.class))
                .thenReturn(dto);

        Map<String, Object> response =
                organizationService.getAllOrganizations(
                        null,
                        null,
                        0,
                        10,
                        "name"
                );

        assertNotNull(response);

        List<?> items = (List<?>) response.get("items");
        PagerDto pager = (PagerDto) response.get("pager");

        assertEquals(1, items.size());
        assertEquals(1, pager.totalElements());
    }

    @Test
    void shouldUpdateOrganizationSuccessfully() {

        UUID id = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(id)
                .name("Old Name")
                .build();

        OrganizationUpdateDto updateDto =
                new OrganizationUpdateDto(
                        "New Name",
                        "Display",
                        true,
                        100L,
                        "https://panex.com",
                        "Delhi",
                        LocalDate.of(2020, 1, 1)
                );

        OrganizationDto response = OrganizationDto.builder()
                .id(id)
                .name("New Name")
                .build();

        when(organizationRepository.findById(id))
                .thenReturn(Optional.of(organization));

        when(organizationRepository.existsByNameIgnoreCaseAndIdNot("New Name", id))
                .thenReturn(false);

        when(organizationRepository.save(any()))
                .thenReturn(organization);

        when(modelMapper.map(any(), eq(OrganizationDto.class)))
                .thenReturn(response);

        OrganizationDto result =
                organizationService.updateOrganization(id, updateDto);

        assertEquals("New Name", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingOrganization() {

        UUID id = UUID.randomUUID();

        when(organizationRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> organizationService.updateOrganization(id, mock(OrganizationUpdateDto.class))
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateName() {

        UUID id = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(id)
                .name("Old Name")
                .build();

        OrganizationUpdateDto updateDto =
                mock(OrganizationUpdateDto.class);

        when(updateDto.name()).thenReturn("Panex");

        when(organizationRepository.findById(id))
                .thenReturn(Optional.of(organization));

        when(organizationRepository.existsByNameIgnoreCaseAndIdNot("Panex", id))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> organizationService.updateOrganization(id, updateDto)
        );
    }

    @Test
    void shouldDeleteOrganizationSuccessfully() {

        UUID id = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(id)
                .build();

        when(organizationRepository.findById(id))
                .thenReturn(Optional.of(organization));

        doNothing().when(organizationRepository).delete(organization);

        organizationService.deleteOrganization(id);

        verify(organizationRepository).delete(organization);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingOrganization() {

        UUID id = UUID.randomUUID();

        when(organizationRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> organizationService.deleteOrganization(id)
        );
    }

    @Test
    void shouldThrowDependentResourceDeleteException() {

        UUID id = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(id)
                .build();

        when(organizationRepository.findById(id))
                .thenReturn(Optional.of(organization));

        doThrow(RuntimeException.class)
                .when(organizationRepository)
                .delete(organization);

        assertThrows(
                DependentResourceDeleteException.class,
                () -> organizationService.deleteOrganization(id)
        );
    }
}