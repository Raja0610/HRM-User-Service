package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.OrganizationUserAssembler;
import com.hrm.project.user_service.dto.OrganizationUserDto;
import com.hrm.project.user_service.dto.PagerDto;
import com.hrm.project.user_service.dto.UserDto;
import com.hrm.project.user_service.entity.Organization;
import com.hrm.project.user_service.entity.OrganizationUser;
import com.hrm.project.user_service.entity.OrganizationUserId;
import com.hrm.project.user_service.entity.User;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.OrganizationRepository;
import com.hrm.project.user_service.repository.OrganizationUserRepository;
import com.hrm.project.user_service.repository.UserRepository;
import com.hrm.project.user_service.service.OrganizationUserService;
import com.hrm.project.user_service.specification.OrganizationUserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service implementation for managing organization users.
 * Handles assignment, update, retrieval, and removal of users within organizations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationUserServiceImpl implements OrganizationUserService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final OrganizationUserAssembler organizationUserAssembler;

    /**
     * Creates a new user (if not exists) and assigns the user to an organization.
     */
    @Override
    public OrganizationUserDto assignUserToOrganization(UUID organizationId, UserDto userDto) {

        log.info("Assigning user with email '{}' to organization '{}'.", userDto.email(), organizationId);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        Optional<User> existingUser = userRepository.findByEmail(userDto.email());

        User user = existingUser.orElse(new User());

        if (existingUser.isPresent()) {

            OrganizationUserId organizationUserId = new OrganizationUserId();
            organizationUserId.setOrganizationId(organizationId);
            organizationUserId.setUserId(user.getId());

            if (organizationUserRepository.existsById(organizationUserId)) {
                log.warn("User with email '{}' already assigned to organization '{}'.", userDto.email(), organizationId);
                throw new ResourceAlreadyExistsException("User already assigned to this organization");
            }
        }

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());
        user.setMobileNumber(userDto.mobileNumber());
        user.setOrganization(organization);

        User savedUser = userRepository.save(user);

        OrganizationUserId organizationUserId = new OrganizationUserId();
        organizationUserId.setOrganizationId(organizationId);
        organizationUserId.setUserId(savedUser.getId());

        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setId(organizationUserId);
        organizationUser.setOrganization(organization);
        organizationUser.setUser(savedUser);

        organizationUserRepository.save(organizationUser);

        log.info("User '{}' successfully assigned to organization '{}'.", savedUser.getId(), organizationId);

        return organizationUserAssembler.toDto(organizationUser);
    }

    /**
     * Updates an existing user within an organization.
     */
    @Override
    public OrganizationUserDto updateOrganizationUser(UUID organizationId, UUID userId, UserDto userDto) {

        log.info("Updating user '{}' in organization '{}'.", userId, organizationId);

        organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        OrganizationUser organizationUser = organizationUserRepository
                .findByOrganizationIdAndUserId(organizationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("OrganizationUser", "id", userId));

        User user = organizationUser.getUser();

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());
        user.setMobileNumber(userDto.mobileNumber());

        userRepository.save(user);

        log.info("User '{}' updated successfully in organization '{}'.", userId, organizationId);

        return organizationUserAssembler.toDto(organizationUser);
    }

    /**
     * Retrieves users of an organization with optional filters and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOrganizationUsers(UUID organizationId, String userName, String email,
                                                    UUID branchId, UUID departmentId,
                                                    int pageNumber, Integer pageSize) {

        log.info("Fetching users for organization '{}'.", organizationId);

        Specification<OrganizationUser> specification = Specification.unrestricted();

        specification = specification.and(OrganizationUserSpecification.hasOrganizationId(organizationId));

        if (Objects.nonNull(userName)) {
            specification = specification.and(OrganizationUserSpecification.hasUserName(userName));
        }
        if (Objects.nonNull(email)) {
            specification = specification.and(OrganizationUserSpecification.hasEmail(email));
        }
        if (Objects.nonNull(branchId)) {
            specification = specification.and(OrganizationUserSpecification.hasBranchId(branchId));
        }
        if (Objects.nonNull(departmentId)) {
            specification = specification.and(OrganizationUserSpecification.hasDepartmentId(departmentId));
        }

        Pageable pageable = (pageSize == null || pageSize <= 0)
                ? Pageable.unpaged()
                : PageRequest.of(pageNumber, pageSize);

        Page<OrganizationUser> page = organizationUserRepository.findAll(specification, pageable);

        List<OrganizationUserDto> organizationUsers = page.stream()
                .map(organizationUserAssembler::toDto)
                .toList();

        PagerDto pager = new PagerDto(page.getTotalElements(), page.getTotalPages());

        log.info("Fetched {} users for organization '{}'.", organizationUsers.size(), organizationId);

        Map<String, Object> response = new HashMap<>();
        response.put("pager", pager);
        response.put("items", organizationUsers);

        return response;
    }

    /**
     * Removes a user from an organization (does not delete the user).
     */
    @Override
    public void removeUserFromOrganization(UUID organizationId, UUID userId) {

        log.info("Removing user '{}' from organization '{}'.", userId, organizationId);

        OrganizationUserId id = new OrganizationUserId();
        id.setOrganizationId(organizationId);
        id.setUserId(userId);

        OrganizationUser organizationUser = organizationUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization user not found"));

        try {
            organizationUserRepository.delete(organizationUser);
            log.info("User '{}' removed successfully from organization '{}'.", userId, organizationId);
        } catch (Exception e) {
            log.error("Failed to remove user '{}' from organization '{}'.", userId, organizationId, e);
            throw new DependentResourceDeleteException("Entity referenced somewhere");
        }
    }
}