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
import com.hrm.project.user_service.service.DepartmentService;
import com.hrm.project.user_service.specification.DepartmentSpecification;
import com.hrm.project.user_service.utils.JsonUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service implementation responsible for Department management operations.
 *
 * <p>
 * Handles:
 * <ul>
 *     <li>Department creation</li>
 *     <li>Department retrieval with filtering and pagination</li>
 *     <li>Department updates</li>
 *     <li>Department deletion</li>
 * </ul>
 * </p>
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final DepartmentAssembler departmentAssembler;

    /**
     * Constructor-based dependency injection.
     *
     * @param departmentRepository department repository
     * @param branchRepository     branch repository
     * @param departmentAssembler  department assembler
     */
    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                 BranchRepository branchRepository,
                                 DepartmentAssembler departmentAssembler) {
        this.departmentRepository = departmentRepository;
        this.branchRepository = branchRepository;
        this.departmentAssembler = departmentAssembler;
    }

    /**
     * Creates a new department under the specified branch.
     *
     * @param branchId      branch identifier
     * @param departmentDto department creation request
     * @return created department
     */
    @Override
    @Transactional
    public DepartmentDto createDepartment(UUID branchId,
                                          DepartmentDto departmentDto) {

        Branch branch = branchRepository.findById(branchId).orElseThrow(
                () -> new ResourceNotFoundException("Branch", "id", branchId)
        );

        if (departmentRepository.existsByNameIgnoreCaseAndBranchId(
                departmentDto.name(),
                branchId
        )) {
            throw new ResourceAlreadyExistsException(
                    "Department already exists with name : "
                            + departmentDto.name()
                            + " under branch : "
                            + branch.getName()
            );
        }

        Department department = new Department();

        department.setName(departmentDto.name());
        department.setDisplayName(departmentDto.displayName());
        department.setActive(departmentDto.active());
        department.setWorkforce(departmentDto.workforce());
        department.setSpecialAttributes(
                JsonUtils.mapToJson(departmentDto.specialAttributes())
        );
        department.setBranch(branch);

        department = departmentRepository.save(department);

        return departmentAssembler.toDto(department);
    }

    /**
     * Retrieves departments using optional filters and pagination.
     *
     * <p>
     * Supports filtering by:
     * <ul>
     *     <li>Department identifier</li>
     *     <li>Department name</li>
     * </ul>
     * </p>
     *
     * @param branchId   branch identifier
     * @param id         optional department identifier
     * @param name       optional department name
     * @param pageNumber page number
     * @param pageSize   page size
     * @param sortBy     sorting field
     * @return paginated department response
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllDepartments(UUID branchId,
                                                 UUID id,
                                                 String name,
                                                 int pageNumber,
                                                 Integer pageSize,
                                                 String sortBy) {

        branchRepository.findById(branchId).orElseThrow(
                () -> new ResourceNotFoundException("Branch", "id", branchId)
        );

        Specification<Department> specification = Specification.unrestricted();

        specification = specification.and(
                DepartmentSpecification.belongsToBranch(branchId)
        );

        if (Objects.nonNull(id)) {
            specification = specification.and(
                    DepartmentSpecification.hasId(id)
            );
        }

        if (Objects.nonNull(name)) {
            specification = specification.and(
                    DepartmentSpecification.hasName(name)
            );
        }

        Pageable pageable = (pageSize == null || pageSize <= 0)
                ? Pageable.unpaged()
                : PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC, sortBy)
        );

        Page<Department> page =
                departmentRepository.findAll(specification, pageable);

        PagerDto pager = new PagerDto(
                page.getTotalElements(),
                page.getTotalPages()
        );

        List<DepartmentDto> departments = page.stream()
                .map(departmentAssembler::toDto)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("pager", pager);
        response.put("items", departments);

        return response;
    }

    /**
     * Updates an existing department.
     *
     * @param branchId      branch identifier
     * @param id            department identifier
     * @param departmentDto updated department details
     * @return updated department
     */
    @Override
    @Transactional
    public DepartmentDto updateDepartment(UUID branchId,
                                          UUID id,
                                          DepartmentDto departmentDto) {

        Department department = departmentRepository
                .findByIdAndBranchId(id, branchId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Department",
                                "id",
                                id
                        )
                );

        if (departmentDto.name() != null
                && departmentRepository
                .existsByNameIgnoreCaseAndBranchIdAndIdNot(
                        departmentDto.name(),
                        branchId,
                        id
                )) {

            throw new ResourceAlreadyExistsException(
                    "Department already exists with name : "
                            + departmentDto.name()
                            + " under this branch"
            );
        }

        department.setName(departmentDto.name());
        department.setDisplayName(departmentDto.displayName());
        department.setActive(departmentDto.active());
        department.setWorkforce(departmentDto.workforce());
        department.setSpecialAttributes(
                JsonUtils.mapToJson(departmentDto.specialAttributes())
        );

        department = departmentRepository.save(department);

        return departmentAssembler.toDto(department);
    }

    /**
     * Deletes an existing department.
     *
     * @param branchId branch identifier
     * @param id       department identifier
     * @throws ResourceNotFoundException if department does not exist
     * @throws DependentResourceDeleteException if department is referenced by another entity
     */
    @Override
    @Transactional
    public void deleteDepartment(UUID branchId, UUID id) {

        Department department = departmentRepository
                .findByIdAndBranchId(id, branchId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Department",
                                "id",
                                id
                        )
                );

        try {
            departmentRepository.delete(department);
        } catch (Exception e) {
            throw new DependentResourceDeleteException(
                    "Entity is already referenced"
            );
        }
    }
}