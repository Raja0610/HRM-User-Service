package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.DepartmentAssembler;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Test
    void createDepartmentSuccess() {

        UUID branchId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);
        branch.setName("Noida Branch");

        Department department = new Department();
        department.setName("Engineering");
        department.setDisplayName("Engineering Department");
        department.setActive(true);
        department.setWorkforce(100L);
        department.setBranch(branch);

        DepartmentDto request = new DepartmentDto(
                null,
                "Engineering",
                "Engineering Department",
                true,
                null,
                null,
                100L,
                Map.of("shift", "Day")
        );

        DepartmentDto response = new DepartmentDto(
                UUID.randomUUID(),
                "Engineering",
                "Engineering Department",
                true,
                branchId,
                "Noida Branch",
                100L,
                Map.of("shift", "Day")
        );

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.of(branch));

        when(departmentRepository.existsByNameIgnoreCaseAndBranchId(
                "Engineering",
                branchId))
                .thenReturn(false);

        when(departmentRepository.save(any(Department.class)))
                .thenReturn(department);

        when(departmentAssembler.toDto(department))
                .thenReturn(response);

        DepartmentDto result =
                departmentService.createDepartment(branchId, request);

        assertNotNull(result);
        assertEquals("Engineering", result.name());

        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartmentBranchNotFound() {

        UUID branchId = UUID.randomUUID();

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.empty());

        DepartmentDto request = new DepartmentDto(
                null,
                "Engineering",
                "Engineering Department",
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
        branch.setName("Noida Branch");

        when(branchRepository.findById(branchId))
                .thenReturn(Optional.of(branch));

        when(departmentRepository.existsByNameIgnoreCaseAndBranchId(
                "Engineering",
                branchId))
                .thenReturn(true);

        DepartmentDto request = new DepartmentDto(
                null,
                "Engineering",
                "Engineering Department",
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
                "Engineering",
                "Engineering Department",
                true,
                branchId,
                "Noida Branch",
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
                        0,
                        10,
                        "name"
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
                        0,
                        10,
                        "name"
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
                "HR",
                "Human Resources",
                true,
                branchId,
                "Noida Branch",
                50L,
                Map.of()
        );

        DepartmentDto response = new DepartmentDto(
                departmentId,
                "HR",
                "Human Resources",
                true,
                branchId,
                "Noida Branch",
                50L,
                Map.of()
        );

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.of(department));

        when(departmentRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot(
                "HR",
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
        assertEquals("HR", result.name());
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
                "HR",
                "Human Resources",
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
                "HR",
                branchId,
                departmentId))
                .thenReturn(true);

        DepartmentDto request = new DepartmentDto(
                null,
                "HR",
                "Human Resources",
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

        when(departmentRepository.findByIdAndBranchId(
                departmentId,
                branchId))
                .thenReturn(Optional.of(department));

        doThrow(new RuntimeException())
                .when(departmentRepository)
                .delete(department);

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
                        "Engineering",
                        0,
                        10,
                        "name"
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

        Map<String, Object> result =
                departmentService.getAllDepartments(
                        branchId,
                        departmentId,
                        null,
                        0,
                        10,
                        "name"
                );

        assertNotNull(result);
    }
}