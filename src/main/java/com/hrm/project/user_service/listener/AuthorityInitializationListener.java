package com.hrm.project.user_service.listener;

import com.hrm.project.user_service.entity.Authority;
import com.hrm.project.user_service.enums.AuthorityType;
import com.hrm.project.user_service.repository.AuthorityRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Seeds missing authority records after the application has started. */
@Component
public class AuthorityInitializationListener {

    private final AuthorityRepository authorityRepository;

    public AuthorityInitializationListener(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeAuthorities() {
        for (AuthorityType authorityType : AuthorityType.values()) {
            if (authorityRepository.existsByName(authorityType)) {
                continue;
            }

            Authority authority = new Authority();
            authority.setName(authorityType);
            authority.setDescription(authorityType.getDescription());
            authorityRepository.save(authority);
        }
    }
}
