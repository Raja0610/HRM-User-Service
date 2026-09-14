package com.hrm.project.user_service.service.impl;

import com.hrm.project.user_service.assembler.UserAssembler;
import com.hrm.project.user_service.dto.UserDto;
import com.hrm.project.user_service.dto.UserProfileDto;
import com.hrm.project.user_service.entity.User;
import com.hrm.project.user_service.exceptions.ResourceAlreadyExistsException;
import com.hrm.project.user_service.exceptions.ResourceNotFoundException;
import com.hrm.project.user_service.repository.UserRepository;
import com.hrm.project.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAssembler userAssembler;

    @Override
    public User createUser(UserDto userDto) {
        log.info("Creating or updating user with email '{}'.", userDto.email());
        Optional<User> existingUser = userRepository.findByEmail(userDto.email());

        User user = existingUser.orElse(new User());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());
        user.setMobileNumber(userDto.mobileNumber());

        User savedUser = userRepository.save(user);
        log.info("User '{}' saved successfully.", savedUser.getId());
        return savedUser;
    }

    @Override
    public UserProfileDto updateUserProfile(UUID userId, UserProfileDto userProfileDto) {
        log.info("Updating profile for user '{}'.", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userRepository.findByEmail(userProfileDto.email())
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    throw new ResourceAlreadyExistsException("User", "email", userProfileDto.email());
                });

        user.setFirstName(userProfileDto.firstName());
        user.setLastName(userProfileDto.lastName());
        user.setMobileNumber(userProfileDto.mobileNumber());
        user.setEmail(userProfileDto.email());
        user.setAddress(userAssembler.toAddress(userProfileDto.address()));

        UserProfileDto savedProfile = userAssembler.toDto(userRepository.save(user));
        log.info("Profile for user '{}' updated successfully.", userId);
        return savedProfile;
    }
}
