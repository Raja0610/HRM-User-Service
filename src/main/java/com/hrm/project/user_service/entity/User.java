package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    /**
     * Default organization associated with the user.
     * <p>
     * This field may become redundant if the application
     * fully adopts the OrganizationUser mapping model
     * for multi-organization support.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    /**
     * Branch to which the user currently belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    /**
     * Department to which the user currently belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}