package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.ResourceAlreadyExistsException;
import com.hrm.project.user_service.dto.OrganizationDto;
import com.hrm.project.user_service.dto.OrganizationUpdateDto;
import com.hrm.project.user_service.dto.PagerDto;
import com.hrm.project.user_service.entity.Organization;


import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.OrganizationRepository;
import com.hrm.project.user_service.service.OrganizationService;
import com.hrm.project.user_service.specification.OrganizationSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service implementation responsible for Organization management operations.
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                   ModelMapper modelMapper) {
        this.organizationRepository = organizationRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Creates a new organization after validating name uniqueness.
     *
     * @param organizationDto organization creation request
     * @return created organization
     */
    @Override
    public OrganizationDto createOrganization(OrganizationDto organizationDto) {

        if (organizationRepository.existsByNameIgnoreCase(organizationDto.getName())) {
            throw new ResourceAlreadyExistsException("Organization already exists with name : " + organizationDto.getName()
            );
        }

        Organization organization = Organization.builder()
                .displayName(organizationDto.getDisplayName())
                .name(organizationDto.getName())
                .totalWorkForce(organizationDto.getTotalWorkForce())
                .headQuarter(organizationDto.getHeadQuarter())
                .establishedYear(organizationDto.getEstablishedYear())
                .active(organizationDto.isActive())
                .website(organizationDto.getWebsite())
                .build();

        organization = organizationRepository.save(organization);

        return modelMapper.map(organization, OrganizationDto.class);
    }

    /**
     * Retrieves organizations using optional filters and pagination.
     *
     * @param id         organization id
     * @param name       organization name
     * @param pageNumber page number
     * @param pageSize   page size
     * @param sortBy     field used for sorting
     * @return response containing pager and items
     */
    @Override
    public Map<String, Object> getAllOrganizations(UUID id,
                                                   String name,
                                                   int pageNumber,
                                                   Integer pageSize,
                                                   String sortBy) {

        Specification<Organization> specification = Specification.unrestricted();

        if (Objects.nonNull(id)) {
            specification = specification.and(OrganizationSpecification.hasId(id));
        }

        if (Objects.nonNull(name)) {
            specification = specification.and(OrganizationSpecification.hasName(name));
        }

        Pageable pageable = (pageSize == null || pageSize <= 0)
                ? Pageable.unpaged()
                : PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortBy));

        Page<Organization> page = organizationRepository.findAll(specification, pageable);

        PagerDto pager = new PagerDto(
                page.getTotalElements(),
                page.getTotalPages()
        );

        List<OrganizationDto> organizations = page.stream()
                .map(organization -> modelMapper.map(organization, OrganizationDto.class))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("pager", pager);
        response.put("items", organizations);
        return response;
    }

    /**
     * Updates an existing organization.
     *
     * @param id                    organization identifier
     * @param organizationUpdateDto updated organization details
     * @return updated organization
     */
    @Override
    public OrganizationDto updateOrganization(UUID id, OrganizationUpdateDto organizationUpdateDto) {

        Organization organization = organizationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Organization", "id", id)
        );

        if (organizationUpdateDto.name() != null && organizationRepository.existsByNameIgnoreCaseAndIdNot(organizationUpdateDto.name(), id)) {
            throw new ResourceAlreadyExistsException("Organization already exists with name : " + organizationUpdateDto.name());
        }

        organization.setName(organizationUpdateDto.name());
        organization.setDisplayName(organizationUpdateDto.displayName());
        organization.setWebsite(organizationUpdateDto.website());
        organization.setHeadQuarter(organizationUpdateDto.headQuarter());
        organization.setEstablishedYear(organizationUpdateDto.establishedYear());
        organization.setActive(organizationUpdateDto.active());

        organization = organizationRepository.save(organization);

        return modelMapper.map(organization, OrganizationDto.class);
    }

    /**
     * Deletes an organization.
     *
     * @param id organization identifier
     */
    @Override
    public void deleteOrganization(UUID id) {

        Organization organization = organizationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Organization", "id", id)
        );

        try {
            organizationRepository.delete(organization);
        } catch (Exception e) {
            throw new DependentResourceDeleteException("Entity is already referenced");
        }
    }
}