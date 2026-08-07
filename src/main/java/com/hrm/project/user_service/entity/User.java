package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a person in the system.
 * <p>
 * A User is a global entity that stores personal information
 * such as name, contact details, and address.
 * <p>
 * Users can be associated with one or more organizations
 * through the OrganizationUser mapping entity.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User's first name.
     */
    @Column(nullable = false, length = 100)
    private String firstName;

    /**
     * User's last name.
     */
    @Column(length = 100)
    private String lastName;

    /**
     * User's primary mobile number.
     */
    @Column(length = 15)
    private String mobileNumber;

    /**
     * User's residential or correspondence address.
     */
    @Embedded
    private Address address;

    /**
     * User's primary email address.
     * <p>
     * Expected to be unique across the system.
     */
    @Column(nullable = false, length = 255)
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<OrganizationUser> organizationUsers = new HashSet<>();
}