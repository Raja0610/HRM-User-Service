package com.hrm.project.user_service.service;

import com.hrm.project.user_service.dto.UserDto;
import com.hrm.project.user_service.dto.UserProfileDto;
import com.hrm.project.user_service.entity.User;

import java.util.UUID;

public interface UserService {
    User createUser(UserDto userDto);

    /**
     * Updates personal data for an existing user. This does not alter organization assignments or roles.
     *
     * @param userId         user identifier
     * @param userProfileDto replacement profile values
     * @return the persisted profile
     */
    UserProfileDto updateUserProfile(UUID userId, UserProfileDto userProfileDto);
}
