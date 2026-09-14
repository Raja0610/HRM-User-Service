package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.utils.JsonUtils;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.assembler.BranchAssembler;
import com.hrm.project.user_service.dto.BranchDto;
import com.hrm.project.user_service.dto.PagerDto;
import com.hrm.project.user_service.entity.Address;
import com.hrm.project.user_service.entity.Branch;
import com.hrm.project.user_service.entity.Organization;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.BranchRepository;
import com.hrm.project.user_service.repository.OrganizationRepository;
import com.hrm.project.user_service.service.BranchService;
import com.hrm.project.user_service.specification.BranchSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service implementation responsible for Branch management operations.
 */
@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final OrganizationRepository organizationRepository;
    private final BranchAssembler branchAssembler;
    private final ModelMapper modelMapper;
    private final ResourceBundleMessageSource messageSource;

    /**
     * Creates a new branch under an organization.
     *
     * @param organizationId organization identifier
     * @param branchDto      branch creation request
     * @return created branch
     */
    @Override
    public BranchDto createBranch(UUID organizationId, BranchDto branchDto) {

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(
                () -> new ResourceNotFoundException("Organization", "id", organizationId)
        );

        if (branchRepository.existsByNameIgnoreCaseAndOrganizationId(branchDto.name(), organizationId)) {
            throw new ResourceAlreadyExistsException("Branch", "name", branchDto.name());
        }

        Branch branch = Branch.builder()
                .name(branchDto.name())
                .displayName(branchDto.displayName())
                .active(branchDto.active())
                .organization(organization)
                .email(branchDto.email())
                .phone(branchDto.phone())
                .address(modelMapper.map(branchDto.address(), Address.class))
                .workForce(branchDto.workForce())
                .specialAttributes(JsonUtils.mapToJson(branchDto.specialAttributes()))
                .establishedIn(branchDto.establishedIn())
                .build();

        branch = branchRepository.save(branch);

        return branchAssembler.toDto(branch);
    }

    /**
     * Retrieves branches using optional filters and pagination.
     */
    @Override
    public Map<String, Object> getAllBranches(UUID organizationId,
                                              UUID id,
                                              String name,
                                              String displayName,
                                              int pageNumber,
                                              Integer pageSize,
                                              String sortBy,
                                              String sortOrder) {

        //Validating the existence of organization
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(
                () -> new ResourceNotFoundException("Organization", "id", organizationId)
        );

        Specification<Branch> specification = Specification.unrestricted();

        specification = specification.and(BranchSpecification.belongsToOrganization(organizationId));

        if (Objects.nonNull(id)) {
            specification = specification.and(BranchSpecification.hasId(id));
        }

        if (Objects.nonNull(name) && !name.isBlank()) {
            specification = specification.and(BranchSpecification.hasName(name));
        }

        if (Objects.nonNull(displayName) && !displayName.isBlank()){
            specification = specification.and(BranchSpecification.hasDisplayName(displayName));
        }

        Pageable pageable = (pageSize == null || pageSize <= 0)
                ? Pageable.unpaged()
                : PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));

        Page<Branch> page = branchRepository.findAll(specification, pageable);

        PagerDto pager = new PagerDto(
                page.getTotalElements(),
                page.getTotalPages()
        );

        List<BranchDto> branches = page.stream()
                .map(branchAssembler::toDto)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("pager", pager);
        response.put("items", branches);

        return response;
    }

    /**
     * Updates an existing branch.
     */
    @Override
    public BranchDto updateBranch(UUID organizationId,
                                  UUID id,
                                  BranchDto branchDto) {

        Branch branch = branchRepository.findByIdAndOrganizationId(id, organizationId).orElseThrow(
                () -> new ResourceNotFoundException("Branch", "id", id)
        );

        if (branchDto.name() != null && branchRepository.existsByNameIgnoreCaseAndOrganizationIdAndIdNot(branchDto.name(), organizationId, id)) {
            throw new ResourceAlreadyExistsException("Branch",  "name", branchDto.name());
        }

        branch.setName(branchDto.name());
        branch.setDisplayName(branchDto.displayName());
        branch.setActive(branchDto.active());
        branch.setEmail(branchDto.email());
        branch.setPhone(branchDto.phone());
        branch.setAddress(modelMapper.map(branchDto.address(), Address.class));
        branch.setWorkForce(branchDto.workForce());
        branch.setSpecialAttributes(JsonUtils.mapToJson(branchDto.specialAttributes()));
        branch.setEstablishedIn(branchDto.establishedIn());

        branch = branchRepository.save(branch);

        return branchAssembler.toDto(branch);
    }

    /**
     * Deletes an existing branch.
     */
    @Override
    public void deleteBranch(UUID organizationId, UUID id) {

        Branch branch = branchRepository.findByIdAndOrganizationId(id, organizationId).orElseThrow(
                () -> new ResourceNotFoundException("Branch", "id", id));

        try {
            branchRepository.delete(branch);
        } catch (Exception e) {
            String message = messageSource == null
                    ? "Branch could not be deleted because it is referenced."
                    : messageSource.getMessage("entity.referenced", null, LocaleContextHolder.getLocale());
            throw new DependentResourceDeleteException(message);
        }
    }
}