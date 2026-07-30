package com.hrm.project.user_service.dto;

import com.hrm.project.user_service.enums.AuthorityType;

import java.util.UUID;

/** API representation of a system authority. */
public record AuthorityDto(UUID id, AuthorityType name, String description) {
}
