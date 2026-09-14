package com.hrm.project.user_service.assembler;

import com.hrm.project.user_service.dto.AddressDto;
import com.hrm.project.user_service.dto.UserProfileDto;
import com.hrm.project.user_service.entity.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAssemblerTest {

    private final UserAssembler userAssembler = new UserAssembler();

    @Test
    void toDto_ShouldMapUserAndAddress() {
        UUID userId = UUID.randomUUID();
        User user = userAssembler.toEntity(profile(userId));
        user.setId(userId);

        UserProfileDto result = userAssembler.toDto(user);

        assertEquals(userId, result.id());
        assertEquals("Jane", result.firstName());
        assertEquals("Pune", result.address().getCity());
    }

    @Test
    void toEntity_ShouldMapProfileAndAddress() {
        User result = userAssembler.toEntity(profile(UUID.randomUUID()));

        assertEquals("Jane", result.getFirstName());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("411001", result.getAddress().getPostalCode());
    }

    /** Builds a complete profile fixture to exercise nested address mapping. */
    private UserProfileDto profile(UUID id) {
        return new UserProfileDto(
                id, "Jane", "Doe", "9876543210", "jane@example.com",
                AddressDto.builder().addressLine1("42 Example Road").city("Pune")
                        .state("Maharashtra").country("India").postalCode("411001").build()
        );
    }
}
