package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.DepartmentAssembler;
import com.hrm.project.user_service.constants.ApplicationConstantsTest;
import com.hrm.project.user_service.dto.DepartmentDto;
import com.hrm.project.user_service.dto.PagerDto;
import com.hrm.project.user_service.entity.Branch;
import com.hrm.project.user_service.entity.Department;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.BranchRepository;
import com.hrm.project.user_service.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private DepartmentAssembler departmentAssembler;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Mock
    private ResourceBundleMessageSource messageSource;

    @Test
    void createDepartmentSuccess() {

        UUID branchId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);
        branch.setName(ApplicationConstantsTest.mockTestDataBranchName);

        Department department = new Department();
        department.setName(ApplicationConstantsTest.mockTestDataDepartmentName);
        department.setDisplayName(ApplicationConstantsTest.mockTestDataDepartmentDisplayName);
        department.setActive(true);
        department.setWorkforce(100L);
        department.setBranch(branch);

        DepartmentDto request = new DepartmentDto(
                null,
                ApplicationConstantsTest.mockTestDataDepartmentName,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayName,
                true,
                null,
                null,
                100L,
                Map.of("shift", "Day")
        );

        DepartmentDto response = new DepartmentDto(
                UUID.randomUUID(),
                ApplicationConstantsTest.mockTestDataDepartmentName,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayName,
                true,
                branchId,
                ApplicationConstantsTest.mockTestDataBranchDisplayName,
                100L,
                Map.of("shift", "Day")
        );

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));

        when(departmentRepository.existsByNameIgnoreCaseAndBranchId(
                ApplicationConstantsTest.mockTestDataDepartmentName,
                branchId))
                .thenReturn(false);

        when(departmentRepository.save(any(Department.class)))
                .thenReturn(department);

        when(departmentAssembler.toDto(department))
                .thenReturn(response);

        DepartmentDto result =
                departmentService.createDepartment(branchId, request);

        assertNotNull(result);
        assertEquals(ApplicationConstantsTest.mockTestDataDepartmentName, result.name());

        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartmentBranchNotFound() {

        UUID branchId = UUID.randomUUID();

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.empty());

        DepartmentDto request = new DepartmentDto(
                null,
                ApplicationConstantsTest.mockTestDataDepartmentName,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayName,
                true,
                null,
                null,
                100L,
                Map.of()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> departmentService.createDepartment(branchId, request)
        );
    }

    @Test
    void createDepartmentDuplicateDepartment() {

        UUID branchId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);
        branch.setName(ApplicationConstantsTest.mockTestDataBranchName);

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.of(branch));

        when(departmentRepository.existsByNameIgnoreCaseAndBranchId(
                ApplicationConstantsTest.mockTestDataDepartmentName,
                branchId))
                .thenReturn(true);

        DepartmentDto request = new DepartmentDto(
                null,
                ApplicationConstantsTest.mockTestDataDepartmentName,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayName,
                true,
                null,
                null,
                100L,
                Map.of()
        );

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> departmentService.createDepartment(branchId, request)
        );
    }

    @Test
    void getAllDepartmentsSuccess() {

        UUID branchId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);

        Department department = new Department();

        DepartmentDto dto = new DepartmentDto(
                UUID.randomUUID(),
                ApplicationConstantsTest.mockTestDataDepartmentName,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayName,
                true,
                branchId,
                ApplicationConstantsTest.mockTestDataBranchName,
                100L,
                Map.of()
        );

        Page<Department> page =
                new PageImpl<>(List.of(department));

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.of(branch));

        when(departmentRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        when(departmentAssembler.toDto(department))
                .thenReturn(dto);

        Map<String, Object> result =
                departmentService.getAllDepartments(
                        branchId,
                        null,
                        null,
                        null,
                        0,
                        10,
                        "name",
                        "asc"
                );

        assertNotNull(result);
        assertTrue(result.containsKey("pager"));
        assertTrue(result.containsKey("items"));

        PagerDto pager = (PagerDto) result.get("pager");

        assertEquals(1L, pager.totalElements());
    }

    @Test
    void getAllDepartmentsBranchNotFound() {

        UUID branchId = UUID.randomUUID();

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> departmentService.getAllDepartments(
                        branchId,
                        null,
                        null,
                        null,
                        0,
                        10,
                        "name",
                        null
                )
        );
    }

    @Test
    void updateDepartmentSuccess() {

        UUID branchId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);

        Department department = new Department();
        department.setId(departmentId);
        department.setBranch(branch);

        DepartmentDto request = new DepartmentDto(
                departmentId,
                ApplicationConstantsTest.mockTestDataDepartmentNameHR,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayNameHR,
                true,
                branchId,
                ApplicationConstantsTest.mockTestDataBranchName,
                50L,
                Map.of()
        );

        DepartmentDto response = new DepartmentDto(
                departmentId,
                ApplicationConstantsTest.mockTestDataDepartmentNameHR,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayNameHR,
                true,
                branchId,
                ApplicationConstantsTest.mockTestDataBranchName,
                50L,
                Map.of()
        );

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.of(department));

        when(departmentRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot(
                ApplicationConstantsTest.mockTestDataDepartmentNameHR,
                branchId,
                departmentId))
                .thenReturn(false);

        when(departmentRepository.save(any(Department.class)))
                .thenReturn(department);

        when(departmentAssembler.toDto(department))
                .thenReturn(response);

        DepartmentDto result =
                departmentService.updateDepartment(
                        branchId,
                        departmentId,
                        request
                );

        assertNotNull(result);
        assertEquals(ApplicationConstantsTest.mockTestDataDepartmentNameHR, result.name());
    }

    @Test
    void updateDepartmentNotFound() {

        UUID branchId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.empty());

        DepartmentDto request = new DepartmentDto(
                null,
                ApplicationConstantsTest.mockTestDataDepartmentNameHR,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayNameHR,
                true,
                null,
                null,
                10L,
                Map.of()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> departmentService.updateDepartment(
                        branchId,
                        departmentId,
                        request
                )
        );
    }

    @Test
    void updateDepartmentDuplicateName() {

        UUID branchId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        Department department = new Department();

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.of(department));

        when(departmentRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot(
                ApplicationConstantsTest.mockTestDataDepartmentNameHR,
                branchId,
                departmentId))
                .thenReturn(true);

        DepartmentDto request = new DepartmentDto(
                null,
                ApplicationConstantsTest.mockTestDataDepartmentNameHR,
                ApplicationConstantsTest.mockTestDataDepartmentDisplayNameHR,
                true,
                null,
                null,
                10L,
                Map.of()
        );

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> departmentService.updateDepartment(
                        branchId,
                        departmentId,
                        request
                )
        );
    }

    @Test
    void deleteDepartmentSuccess() {

        UUID branchId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        Department department = new Department();

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.of(department));

        assertDoesNotThrow(
                () -> departmentService.deleteDepartment(
                        branchId,
                        departmentId
                )
        );

        verify(departmentRepository).delete(department);
    }

    @Test
    void deleteDepartmentNotFound() {

        UUID branchId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> departmentService.deleteDepartment(
                        branchId,
                        departmentId
                )
        );
    }

    @Test
    void deleteDepartmentDependentResourceExists() {

        UUID branchId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        Department department = new Department();
        department.setId(departmentId);

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.of(department));

        doThrow(new RuntimeException())
                .when(departmentRepository)
                .delete(department);

        when(messageSource.getMessage(
                eq("entity.referenced"),
                isNull(),
                any(Locale.class)
        )).thenReturn("Branch could not be deleted because it is referenced.");


        assertThrows(
                DependentResourceDeleteException.class,
                () -> departmentService.deleteDepartment(
                        branchId,
                        departmentId
                )
        );
    }

    @Test
    void getAllDepartmentsWithNameFilter() {

        UUID branchId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.of(branch));

        Page<Department> page = new PageImpl<>(List.of());

        when(departmentRepository.findAll(
                Mockito.<Specification<Department>>any(),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Map<String, Object> result =
                departmentService.getAllDepartments(
                        branchId,
                        null,
                        ApplicationConstantsTest.mockTestDataDepartmentName,
                        null,
                        0,
                        10,
                        "name",
                        "asc"
                );

        assertNotNull(result);
    }

    @Test
    void getAllDepartmentsWithIdFilter() {

        UUID branchId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.of(branch));

        Page<Department> page = new PageImpl<>(List.of());

        when(departmentRepository.findAll(
                Mockito.<Specification<Department>>any(),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Map<String, Object> result = departmentService.getAllDepartments(
                branchId,
                departmentId,
                null,
                null,
                0,
                10,
                "name",
                "asc"
        );

        assertNotNull(result);
    }
}