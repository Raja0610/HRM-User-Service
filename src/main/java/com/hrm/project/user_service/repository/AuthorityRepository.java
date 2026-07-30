package com.hrm.project.user_service.repository;

import com.hrm.project.user_service.entity.Authority;
import com.hrm.project.user_service.enums.AuthorityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

    boolean existsByName(AuthorityType name);
}
