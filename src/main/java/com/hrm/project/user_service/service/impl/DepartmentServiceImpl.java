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
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
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
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final DepartmentAssembler departmentAssembler;
    private final ResourceBundleMessageSource messageSource;

    /**
     * Creates a new department under the specified branch.
     *
     * @param branchId      branch identifier
     * @param departmentDto department creation request
     * @return created department
     */
    @Override
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
                    "Department", "name", departmentDto.name()
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
     * @param branchId    branch identifier
     * @param id          optional department identifier
     * @param name        optional department name
     * @param displayName optional department display name
     * @param pageNumber  page number
     * @param pageSize    page size
     * @param sortBy      sorting field
     * @param sortOrder   sorting order
     * @return paginated department response
     */
    @Override
    public Map<String, Object> getAllDepartments(UUID branchId,
                                                 UUID id,
                                                 String name,
                                                 String displayName,
                                                 int pageNumber,
                                                 Integer pageSize,
                                                 String sortBy,
                                                 String sortOrder) {

        branchRepository.findById(branchId).orElseThrow(
                () -> new ResourceNotFoundException("Branch", "id", branchId)
        );

        Specification<Department> specification = Specification.unrestricted();

        specification = specification.and(DepartmentSpecification.belongsToBranch(branchId));

        if (Objects.nonNull(id)) {
            specification = specification.and(DepartmentSpecification.hasId(id));
        }

        if (Objects.nonNull(name) && !name.isBlank()) {
            specification = specification.and(DepartmentSpecification.hasName(name));
        }

        if (Objects.nonNull(displayName) && !displayName.isBlank()) {
            specification = specification.and(DepartmentSpecification.hasDisplayName(displayName));
        }

        Pageable pageable = (pageSize == null || pageSize <= 0)
                ? Pageable.unpaged()
                : PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.fromString(sortOrder), sortBy)
        );

        Page<Department> page = departmentRepository.findAll(specification, pageable);

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
                    "Department", "name", departmentDto.name()
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
     * @throws ResourceNotFoundException        if department does not exist
     * @throws DependentResourceDeleteException if department is referenced by another entity
     */
    @Override
    public void deleteDepartment(UUID branchId, UUID id) {

        Department department = departmentRepository.findByIdAndBranchId(id, branchId).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Department",
                                "id",
                                id
                        )
                );

        try {
            departmentRepository.delete(department);
        } catch (Exception e) {
            throw new DependentResourceDeleteException(messageSource.getMessage("entity.referenced", null, LocaleContextHolder.getLocale()));
        }
    }
}