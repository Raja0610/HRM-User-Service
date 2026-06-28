package com.hrm.project.user_service.repository;

import com.hrm.project.user_service.entity.OrganizationUser;
import com.hrm.project.user_service.entity.OrganizationUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, OrganizationUserId>, JpaSpecificationExecutor<OrganizationUser> {
    Optional<OrganizationUser> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}
