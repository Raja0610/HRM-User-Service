package com.hrm.project.user_service.repository;

import com.hrm.project.user_service.entity.Organization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID>, JpaSpecificationExecutor<Organization> {
    boolean existsByNameIgnoreCase(@NotBlank(message = "Organization name must not be blank") @Size(max = 255, message = "Organization name must not exceed 255 characters") String name);

    boolean existsByNameIgnoreCaseAndIdNot(@NotBlank(message = "Organization name is required") @Size(max = 255, message = "Organization name cannot exceed 255 characters") String name, UUID id);
}
