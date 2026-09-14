package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.UserAssembler;
import com.hrm.project.user_service.dto.AddressDto;
import com.hrm.project.user_service.dto.UserProfileDto;
import com.hrm.project.user_service.entity.User;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAssembler userAssembler;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updateUserProfile_ShouldPersistAndReturnUpdatedProfile() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        UserProfileDto profile = profile(userId, "jane@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(profile.email())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userAssembler.toDto(user)).thenReturn(profile);

        UserProfileDto result = userService.updateUserProfile(userId, profile);

        assertEquals(userId, result.id());
        assertEquals("Jane", result.firstName());
        assertEquals("Pune", result.address().getCity());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserProfile_ShouldThrowWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserProfile(userId, profile(userId, "jane@example.com")));

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserProfile_ShouldRejectAnEmailOwnedByAnotherUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        UserProfileDto profile = profile(userId, "taken@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(profile.email())).thenReturn(Optional.of(otherUser));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUserProfile(userId, profile));

        verify(userRepository, never()).save(any());
    }

    private UserProfileDto profile(UUID id, String email) {
        return new UserProfileDto(
                id,
                "Jane",
                "Doe",
                "9876543210",
                email,
                AddressDto.builder()
                        .addressLine1("42 Example Road")
                        .city("Pune")
                        .state("Maharashtra")
                        .country("India")
                        .postalCode("411001")
                        .build()
        );
    }
}
