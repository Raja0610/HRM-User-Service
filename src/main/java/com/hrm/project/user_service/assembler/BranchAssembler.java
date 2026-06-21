package com.hrm.project.user_service.assembler;

import com.hrm.project.user_service.JsonUtils;
import com.hrm.project.user_service.dto.AddressDto;
import com.hrm.project.user_service.dto.BranchDto;
import com.hrm.project.user_service.entity.Address;
import com.hrm.project.user_service.entity.Branch;
import org.springframework.stereotype.Component;

/**
 * Assembler responsible for converting between
 * {@link Branch} entities and {@link BranchDto} objects.
 *
 * <p>
 * Handles:
 * <ul>
 *     <li>Branch entity to DTO conversion</li>
 *     <li>Branch DTO to entity conversion</li>
 *     <li>Address entity to DTO conversion</li>
 *     <li>Address DTO to entity conversion</li>
 *     <li>JSON conversion of special attributes</li>
 * </ul>
 * </p>
 */
@Component
public class BranchAssembler implements BaseAssembler<Branch, BranchDto> {

    /**
     * Converts a Branch entity into a BranchDto.
     *
     * @param branch source branch entity
     * @return mapped BranchDto
     */
    @Override
    public BranchDto toDto(Branch branch) {

        return new BranchDto(
                branch.getId(),
                branch.getName(),
                branch.getDisplayName(),
                branch.isActive(),
                branch.getOrganization().getId(),
                branch.getOrganization().getName(),
                branch.getEmail(),
                branch.getPhone(),
                toAddressDto(branch.getAddress()),
                branch.getWorkForce(),
                JsonUtils.jsonToMap(branch.getSpecialAttributes()),
                branch.getEstablishedIn()
        );
    }

    /**
     * Converts a BranchDto into a Branch entity.
     *
     * <p>
     * Organization mapping is intentionally excluded here
     * because the Organization entity should be resolved
     * and assigned by the service layer.
     * </p>
     *
     * @param branchDto source branch DTO
     * @return mapped Branch entity
     */
    @Override
    public Branch toEntity(BranchDto branchDto) {

        return Branch.builder()
                .name(branchDto.name())
                .displayName(branchDto.displayName())
                .active(branchDto.active())
                .email(branchDto.email())
                .phone(branchDto.phone())
                .address(toAddress(branchDto.address()))
                .workForce(branchDto.workForce())
                .specialAttributes(JsonUtils.mapToJson(branchDto.specialAttributes()))
                .establishedIn(branchDto.establishedIn())
                .build();
    }

    /**
     * Converts an Address entity into an AddressDto.
     *
     * @param address source address entity
     * @return mapped AddressDto
     */
    private AddressDto toAddressDto(Address address) {

        if (address == null) {
            return null;
        }

        return new AddressDto(
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPostalCode()
        );
    }

    /**
     * Converts an AddressDto into an Address entity.
     *
     * @param addressDto source address DTO
     * @return mapped Address entity
     */
    private Address toAddress(AddressDto addressDto) {

        if (addressDto == null) {
            return null;
        }

        return Address.builder()
                .addressLine1(addressDto.getAddressLine1())
                .addressLine2(addressDto.getAddressLine2())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .country(addressDto.getCountry())
                .postalCode(addressDto.getPostalCode())
                .build();
    }
}