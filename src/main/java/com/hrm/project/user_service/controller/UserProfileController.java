package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.UserProfileDto;
import com.hrm.project.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for a user's personal profile. Organization membership and roles
 * remain the responsibility of {@link OrganizationUserController}.
 */
@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users/{userId}/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    /**
     * Replaces the personal profile for the specified user.
     *
     * @param userId         user identifier
     * @param userProfileDto profile information to persist
     * @return the persisted profile
     */
    @PutMapping
    public ResponseEntity<UserProfileDto> updateUserProfile(@PathVariable UUID userId,
                                                            @Valid @RequestBody UserProfileDto userProfileDto) {
        log.info("Updating profile for user '{}'.", userId);

        UserProfileDto response = userService.updateUserProfile(userId, userProfileDto);

        log.info("Profile for user '{}' updated successfully.", userId);
        return ResponseEntity.ok(response);
    }
}
