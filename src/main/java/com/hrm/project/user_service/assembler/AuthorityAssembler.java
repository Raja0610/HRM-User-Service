package com.hrm.project.user_service.assembler;

import com.hrm.project.user_service.dto.AuthorityDto;
import com.hrm.project.user_service.entity.Authority;
import org.springframework.stereotype.Component;

/** Converts authorities to their API representation. */
@Component
public class AuthorityAssembler implements BaseAssembler<Authority, AuthorityDto> {

    @Override
    public AuthorityDto toDto(Authority authority) {
        if (authority == null) {
            return null;
        }
        return new AuthorityDto(authority.getId(), authority.getName(), authority.getDescription());
    }

    @Override
    public Authority toEntity(AuthorityDto authorityDto) {
        if (authorityDto == null) {
            return null;
        }
        Authority authority = new Authority();
        authority.setName(authorityDto.name());
        authority.setDescription(authorityDto.description());
        return authority;
    }
}
