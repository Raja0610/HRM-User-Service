package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "branch")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private boolean active;

    /**
     * Parent organization to which this branch belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column
    private String email;

    @Column(length = 20)
    private String phone;

    /**
     * Branch address information.
     */
    @Embedded
    private Address address;

    @Column
    private Long workForce = 0L;

    /**
     * Dynamic attributes specific to an organization/branch.
     * Example:
     * {
     * "branchType": "Warehouse",
     * "shiftCount": 3
     * }
     */
    @Column(columnDefinition = "text")
    private String specialAttributes;

    @Column
    private LocalDate establishedIn;
}