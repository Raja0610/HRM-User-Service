package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.BranchAssembler;
import com.hrm.project.user_service.dto.AddressDto;
import com.hrm.project.user_service.dto.BranchDto;
import com.hrm.project.user_service.dto.PagerDto;
import com.hrm.project.user_service.entity.Address;
import com.hrm.project.user_service.entity.Branch;
import com.hrm.project.user_service.entity.Organization;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.BranchRepository;
import com.hrm.project.user_service.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private BranchAssembler branchAssembler;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BranchServiceImpl branchService;

    private AddressDto getAddressDto() {
        return AddressDto.builder()
                .addressLine1("Sector 62")
                .addressLine2("Block A")
                .city("Noida")
                .state("UP")
                .country("India")
                .postalCode("201301")
                .build();
    }

    private Organization getOrganization(UUID organizationId) {
        return Organization.builder()
                .id(organizationId)
                .name("Panex")
                .displayName("Panex Logistics")
                .active(true)
                .build();
    }

    private BranchDto getBranchDto(UUID organizationId) {
        return new BranchDto(
                null,
                "Noida Branch",
                "Noida Branch",
                true,
                organizationId,
                null,
                "branch@test.com",
                "9999999999",
                getAddressDto(),
                100L,
                Map.of("type", "Warehouse"),
                LocalDate.now()
        );
    }

    @Test
    void shouldThrowExceptionWhenOrganizationNotFoundDuringCreate() {

        UUID organizationId = UUID.randomUUID();

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> branchService.createBranch(
                        organizationId,
                        getBranchDto(organizationId)
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchAlreadyExists() {

        UUID organizationId = UUID.randomUUID();

        Organization organization = getOrganization(organizationId);

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(branchRepository.existsByNameIgnoreCaseAndOrganizationId(
                "Noida Branch",
                organizationId
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> branchService.createBranch(
                        organizationId,
                        getBranchDto(organizationId)
                )
        );
    }

    @Test
    void shouldReturnBranchByNameFilter() {

        UUID organizationId = UUID.randomUUID();

        Organization organization = getOrganization(organizationId);

        Branch branch = Branch.builder()
                .id(UUID.randomUUID())
                .name("Noida Branch")
                .organization(organization)
                .build();

        BranchDto dto = getBranchDto(organizationId);

        Page<Branch> page = new PageImpl<>(List.of(branch));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(branchRepository.findAll(
                ArgumentMatchers.<Specification<Branch>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        when(branchAssembler.toDto(branch))
                .thenReturn(dto);

        Map<String, Object> response =
                branchService.getAllBranches(
                        organizationId,
                        null,
                        "Noida Branch",
                        0,
                        10,
                        "name"
                );

        List<?> items = (List<?>) response.get("items");

        assertEquals(1, items.size());
    }

    @Test
    void shouldUpdateBranchSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Organization organization = getOrganization(organizationId);

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Old Branch")
                .organization(organization)
                .build();

        BranchDto request = getBranchDto(organizationId);
        BranchDto response = getBranchDto(organizationId);

        Address address = Address.builder()
                .city("Noida")
                .build();

        when(branchRepository.findByIdAndOrganizationId(
                branchId,
                organizationId
        )).thenReturn(Optional.of(branch));

        when(branchRepository.existsByNameIgnoreCaseAndOrganizationIdAndIdNot(
                "Noida Branch",
                organizationId,
                branchId
        )).thenReturn(false);

        when(modelMapper.map(any(AddressDto.class), eq(Address.class)))
                .thenReturn(address);

        when(branchRepository.save(any(Branch.class)))
                .thenReturn(branch);

        when(branchAssembler.toDto(branch))
                .thenReturn(response);

        BranchDto result =
                branchService.updateBranch(
                        organizationId,
                        branchId,
                        request
                );

        assertNotNull(result);

        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void shouldThrowExceptionWhenOrganizationNotFoundDuringGetAll() {

        UUID organizationId = UUID.randomUUID();

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> branchService.getAllBranches(
                        organizationId,
                        null,
                        null,
                        0,
                        10,
                        "name"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingBranch() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        when(branchRepository.findByIdAndOrganizationId(
                branchId,
                organizationId
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> branchService.updateBranch(
                        organizationId,
                        branchId,
                        getBranchDto(organizationId)
                )
        );
    }

    @Test
    void shouldCreateBranchSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Organization organization = getOrganization(organizationId);

        BranchDto request = getBranchDto(organizationId);

        Address address = Address.builder()
                .city("Noida")
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Noida Branch")
                .displayName("Noida Branch")
                .organization(organization)
                .address(address)
                .active(true)
                .build();

        BranchDto response = new BranchDto(
                branchId,
                "Noida Branch",
                "Noida Branch",
                true,
                organizationId,
                "Panex",
                "branch@test.com",
                "9999999999",
                getAddressDto(),
                100L,
                Map.of("type", "Warehouse"),
                LocalDate.now()
        );

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(branchRepository.existsByNameIgnoreCaseAndOrganizationId(
                "Noida Branch",
                organizationId
        )).thenReturn(false);

        when(modelMapper.map(any(AddressDto.class), eq(Address.class)))
                .thenReturn(address);

        when(branchRepository.save(any(Branch.class)))
                .thenReturn(branch);

        when(branchAssembler.toDto(branch))
                .thenReturn(response);

        BranchDto result =
                branchService.createBranch(organizationId, request);

        assertNotNull(result);
        assertEquals(branchId, result.id());

        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void shouldReturnBranchesSuccessfully() {

        UUID organizationId = UUID.randomUUID();

        Organization organization = getOrganization(organizationId);

        Branch branch = Branch.builder()
                .id(UUID.randomUUID())
                .name("Noida Branch")
                .organization(organization)
                .build();

        BranchDto dto = getBranchDto(organizationId);

        Page<Branch> page = new PageImpl<>(List.of(branch));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(branchRepository.findAll(
                ArgumentMatchers.<Specification<Branch>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        when(branchAssembler.toDto(branch))
                .thenReturn(dto);

        Map<String, Object> response =
                branchService.getAllBranches(
                        organizationId,
                        null,
                        null,
                        0,
                        10,
                        "name"
                );

        List<?> items = (List<?>) response.get("items");
        PagerDto pager = (PagerDto) response.get("pager");

        assertEquals(1, items.size());
        assertEquals(1, pager.totalElements());
        assertEquals(1, pager.totalPages());
    }

    @Test
    void shouldReturnBranchesWithoutPagination() {

        UUID organizationId = UUID.randomUUID();

        Organization organization = getOrganization(organizationId);

        Branch branch = Branch.builder()
                .id(UUID.randomUUID())
                .name("Noida Branch")
                .organization(organization)
                .build();

        BranchDto dto = getBranchDto(organizationId);

        Page<Branch> page = new PageImpl<>(List.of(branch));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(branchRepository.findAll(
                ArgumentMatchers.<Specification<Branch>>any(),
                eq(Pageable.unpaged())
        )).thenReturn(page);

        when(branchAssembler.toDto(branch))
                .thenReturn(dto);

        Map<String, Object> response =
                branchService.getAllBranches(
                        organizationId,
                        null,
                        null,
                        0,
                        null,
                        "name"
                );

        List<?> items = (List<?>) response.get("items");
        PagerDto pager = (PagerDto) response.get("pager");

        assertEquals(1, items.size());
        assertEquals(1, pager.totalElements());
    }

    @Test
    void shouldReturnBranchByIdFilter() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Organization organization = getOrganization(organizationId);

        Branch branch = Branch.builder()
                .id(branchId)
                .organization(organization)
                .build();

        BranchDto dto = getBranchDto(organizationId);

        Page<Branch> page = new PageImpl<>(List.of(branch));

        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));

        when(branchRepository.findAll(
                ArgumentMatchers.<Specification<Branch>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        when(branchAssembler.toDto(branch))
                .thenReturn(dto);

        Map<String, Object> response =
                branchService.getAllBranches(
                        organizationId,
                        branchId,
                        null,
                        0,
                        10,
                        "name"
                );

        List<?> items = (List<?>) response.get("items");

        assertEquals(1, items.size());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateBranchName() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Branch branch = Branch.builder()
                .id(branchId)
                .build();

        when(branchRepository.findByIdAndOrganizationId(
                branchId,
                organizationId
        )).thenReturn(Optional.of(branch));

        when(branchRepository.existsByNameIgnoreCaseAndOrganizationIdAndIdNot(
                "Noida Branch",
                organizationId,
                branchId
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> branchService.updateBranch(
                        organizationId,
                        branchId,
                        getBranchDto(organizationId)
                )
        );
    }

    @Test
    void shouldDeleteBranchSuccessfully() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Branch branch = Branch.builder()
                .id(branchId)
                .build();

        when(branchRepository.findByIdAndOrganizationId(
                branchId,
                organizationId
        )).thenReturn(Optional.of(branch));

        doNothing().when(branchRepository).delete(branch);

        branchService.deleteBranch(
                organizationId,
                branchId
        );

        verify(branchRepository).delete(branch);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingBranch() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        when(branchRepository.findByIdAndOrganizationId(
                branchId,
                organizationId
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> branchService.deleteBranch(
                        organizationId,
                        branchId
                )
        );
    }

    @Test
    void shouldThrowDependentResourceDeleteException() {

        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Branch branch = Branch.builder()
                .id(branchId)
                .build();

        when(branchRepository.findByIdAndOrganizationId(
                branchId,
                organizationId
        )).thenReturn(Optional.of(branch));

        doThrow(RuntimeException.class)
                .when(branchRepository)
                .delete(branch);

        assertThrows(
                DependentResourceDeleteException.class,
                () -> branchService.deleteBranch(
                        organizationId,
                        branchId
                )
        );
    }
}