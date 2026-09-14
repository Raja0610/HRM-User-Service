package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an organization in the HRM system.
 * <p>
 * This entity stores basic organization details such as name, display name,
 * status, workforce size, website, headquarters, and establishment year.
 */
@Entity
@Table(name = "organization")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Organization {

    /**
     * Primary identifier of the organization.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    /**
     * Official name of the organization.
     * Used for maintaining unique organization within system
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Unique display name to describe organization identity.
     */
    @Column(nullable = false)
    private String displayName;

    /**
     * Indicates whether the organization is currently active.
     */
    @Column
    private boolean active;

    /**
     * Total number of people associated with the organization workforce.
     * <p>
     * Defaults to 0 when not explicitly provided.
     */
    @Column(name = "current_workforce")
    private Long totalWorkForce = 0L;

    /**
     * Official website URL of the organization.
     */
    @Column
    private String website;

    /**
     * Headquarter location of the organization.
     */
    @Column
    private String headQuarter;

    /**
     * Year or date on which the organization was established.
     */
    @Column
    private LocalDate establishedYear;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branch> branches = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationUser> organizationUsers = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Role> roles = new ArrayList<>();
}