package com.hrm.project.user_service.service;

import com.hrm.project.user_service.dto.AuthorityDto;

import java.util.List;
import java.util.UUID;

public interface AuthorityService {

    List<AuthorityDto> getAuthorities();

    void deleteAuthority(UUID authorityId);
}
