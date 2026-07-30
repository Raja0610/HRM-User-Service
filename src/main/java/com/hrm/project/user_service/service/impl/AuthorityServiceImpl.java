package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.AuthorityAssembler;
import com.hrm.project.user_service.dto.AuthorityDto;
import com.hrm.project.user_service.entity.Authority;
import com.hrm.project.user_service.exceptions.DependentResourceDeleteException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.AuthorityRepository;
import com.hrm.project.user_service.service.AuthorityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityAssembler authorityAssembler;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository, AuthorityAssembler authorityAssembler) {
        this.authorityRepository = authorityRepository;
        this.authorityAssembler = authorityAssembler;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityDto> getAuthorities() {
        return authorityRepository.findAll().stream().map(authorityAssembler::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteAuthority(UUID authorityId) {
        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new ResourceNotFoundException("Authority", "id", authorityId));
        try {
            new HashSet<>(authority.getRoles()).forEach(role -> role.getAuthorities().remove(authority));
            authorityRepository.delete(authority);
            authorityRepository.flush();
        } catch (Exception exception) {
            throw new DependentResourceDeleteException("Authority could not be deleted because it is referenced");
        }
    }
}
