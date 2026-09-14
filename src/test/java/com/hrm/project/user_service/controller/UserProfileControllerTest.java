package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.UserProfileDto;
import com.hrm.project.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserProfileController userProfileController;

    @Test
    void updateUserProfile_ShouldDelegateToUserServiceAndReturnOk() {
        UUID userId = UUID.randomUUID();
        UserProfileDto profile = new UserProfileDto(
                userId, "Jane", "Doe", "9876543210", "jane@example.com", null);
        when(userService.updateUserProfile(userId, profile)).thenReturn(profile);

        ResponseEntity<UserProfileDto> response = userProfileController.updateUserProfile(userId, profile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profile, response.getBody());
        verify(userService).updateUserProfile(userId, profile);
    }
}
