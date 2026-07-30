package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.RoleAssembler;
import com.hrm.project.user_service.dto.PagerDto;
import com.hrm.project.user_service.dto.RoleDto;
import com.hrm.project.user_service.entity.Organization;
import com.hrm.project.user_service.entity.Role;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.OrganizationRepository;
import com.hrm.project.user_service.repository.RoleRepository;
import com.hrm.project.user_service.service.RoleService;
import com.hrm.project.user_service.specification.RoleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleAssembler roleAssembler;

    public RoleServiceImpl(RoleRepository roleRepository,
                           OrganizationRepository organizationRepository,
                           RoleAssembler roleAssembler) {
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.roleAssembler = roleAssembler;
    }

    @Override
    @Transactional
    public RoleDto createRole(UUID organizationId, RoleDto roleDto) {
        Organization organization = findOrganization(organizationId);
        ensureUniqueRoleFields(organizationId, roleDto, null);

        Role role = roleAssembler.toEntity(roleDto);
        role.setOrganization(organization);
        return roleAssembler.toDto(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRoles(UUID organizationId, UUID id, String name, String displayName,
                                        int pageNumber, Integer pageSize, String sortBy) {
        findOrganization(organizationId);
        Specification<Role> specification = Specification.unrestricted();
        specification = specification.and(RoleSpecification.belongsToOrganization(organizationId));
        if (Objects.nonNull(id)) {
            specification = specification.and(RoleSpecification.hasId(id));
        }
        if (Objects.nonNull(name)) {
            specification = specification.and(RoleSpecification.hasName(name));
        }
        if (Objects.nonNull(displayName)) {
            specification = specification.and(RoleSpecification.hasDisplayName(displayName));
        }
        Pageable pageable = pageSize == null || pageSize <= 0
                ? Pageable.unpaged()
                : PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortBy));
        Page<Role> page = roleRepository.findAll(specification, pageable);
        List<RoleDto> roles = page.stream().map(roleAssembler::toDto).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("pager", new PagerDto(page.getTotalElements(), page.getTotalPages()));
        response.put("items", roles);
        return response;
    }

    @Override
    @Transactional
    public RoleDto updateRole(UUID organizationId, UUID roleId, RoleDto roleDto) {
        Role role = findRole(organizationId, roleId);
        ensureUniqueRoleFields(organizationId, roleDto, roleId);
        role.setName(roleDto.name());
        role.setDisplayName(roleDto.displayName());
        role.setDescription(roleDto.description());
        return roleAssembler.toDto(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void deleteRole(UUID organizationId, UUID roleId) {
        Role role = findRole(organizationId, roleId);
        try {
            new HashSet<>(role.getOrganizationUsers())
                    .forEach(organizationUser -> organizationUser.getRoles().remove(role));
            roleRepository.delete(role);
            roleRepository.flush();
        } catch (Exception exception) {
            throw new DependentResourceDeleteException("Role could not be deleted because it is referenced");
        }
    }

    private Organization findOrganization(UUID organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));
    }

    private Role findRole(UUID organizationId, UUID roleId) {
        return roleRepository.findByIdAndOrganizationId(roleId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
    }

    private void ensureUniqueRoleFields(UUID organizationId, RoleDto roleDto, UUID roleId) {
        boolean duplicateName = roleId == null
                ? roleRepository.existsByNameIgnoreCase(roleDto.name())
                : roleRepository.existsByNameIgnoreCaseAndIdNot(roleDto.name(), roleId);
        if (duplicateName) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + roleDto.name());
        }
    }
}
