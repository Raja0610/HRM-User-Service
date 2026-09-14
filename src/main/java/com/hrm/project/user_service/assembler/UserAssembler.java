package com.hrm.project.user_service.assembler;

import com.hrm.project.user_service.dto.AddressDto;
import com.hrm.project.user_service.dto.UserProfileDto;
import com.hrm.project.user_service.entity.Address;
import com.hrm.project.user_service.entity.User;
import org.springframework.stereotype.Component;

/**
 * Converts {@link User} entities and {@link UserProfileDto} objects, including
 * the user's embedded address.
 */
@Component
public class UserAssembler implements BaseAssembler<User, UserProfileDto> {

    /** Maps a user entity to its profile representation. */
    @Override
    public UserProfileDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserProfileDto(user.getId(), user.getFirstName(), user.getLastName(),
                user.getMobileNumber(), user.getEmail(), toAddressDto(user.getAddress()));
    }

    /** Maps profile fields to a new user entity; identity remains service-layer responsibility. */
    @Override
    public User toEntity(UserProfileDto userProfileDto) {
        if (userProfileDto == null) {
            return null;
        }

        User user = new User();
        user.setFirstName(userProfileDto.firstName());
        user.setLastName(userProfileDto.lastName());
        user.setMobileNumber(userProfileDto.mobileNumber());
        user.setEmail(userProfileDto.email());
        user.setAddress(toAddress(userProfileDto.address()));
        return user;
    }

    /** Maps an address DTO to the embeddable entity used by {@link User}. */
    public Address toAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }

        return Address.builder().addressLine1(addressDto.getAddressLine1())
                .addressLine2(addressDto.getAddressLine2()).city(addressDto.getCity())
                .state(addressDto.getState()).country(addressDto.getCountry())
                .postalCode(addressDto.getPostalCode()).build();
    }

    /** Maps an embedded address entity to its DTO representation. */
    public AddressDto toAddressDto(Address address) {
        if (address == null) {
            return null;
        }

        return AddressDto.builder().addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2()).city(address.getCity())
                .state(address.getState()).country(address.getCountry())
                .postalCode(address.getPostalCode()).build();
    }
}
