package com.hrm.project.user_service.assembler;

import com.hrm.project.user_service.dto.DepartmentDto;
import com.hrm.project.user_service.entity.Department;
import com.hrm.project.user_service.utils.JsonUtils;
import org.springframework.stereotype.Component;

/**
 * Assembler responsible for converting between
 * {@link Department} entities and {@link DepartmentDto} objects.
 *
 * <p>
 * Handles:
 * <ul>
 *     <li>Department entity to DTO conversion</li>
 *     <li>Department DTO to entity conversion</li>
 *     <li>JSON conversion of special attributes</li>
 * </ul>
 * </p>
 */
@Component
public class DepartmentAssembler implements BaseAssembler<Department, DepartmentDto> {

    /**
     * Converts a Department entity into a DepartmentDto.
     *
     * @param department source department entity
     * @return mapped DepartmentDto
     */
    @Override
    public DepartmentDto toDto(Department department) {

        if (department == null) {
            return null;
        }

        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getDisplayName(),
                department.getActive(),
                department.getBranch() != null ? department.getBranch().getId() : null,
                department.getBranch() != null ? department.getBranch().getName() : null,
                department.getWorkforce(),
                JsonUtils.jsonToMap(department.getSpecialAttributes())
        );
    }

    /**
     * Converts a DepartmentDto into a Department entity.
     *
     * <p>
     * Branch mapping is intentionally excluded here because
     * the Branch entity should be resolved and assigned by
     * the service layer.
     * </p>
     *
     * @param departmentDto source department DTO
     * @return mapped Department entity
     */
    @Override
    public Department toEntity(DepartmentDto departmentDto) {

        if (departmentDto == null) {
            return null;
        }

        Department department = new Department();

        department.setName(departmentDto.name());
        department.setDisplayName(departmentDto.displayName());
        department.setActive(departmentDto.active());
        department.setWorkforce(departmentDto.workforce());
        department.setSpecialAttributes(
                JsonUtils.mapToJson(departmentDto.specialAttributes())
        );

        return department;
    }
}