package com.hrm.project.user_service.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;
}