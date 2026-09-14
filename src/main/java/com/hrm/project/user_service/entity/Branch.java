package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    @Column
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String displayName;

    @Column
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

    @Column(name = "current_workforce")
    private Long workForce = 0L;

    /**
     * Dynamic attributes specific to an organization/branch.
     * Example:
     * {
     * "branchType": "Warehouse",
     * "shiftCount": 3
     * }
     */
    @Column(columnDefinition = "TEXT")
    private String specialAttributes;

    @Column
    private LocalDate establishedIn;

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> roles = new ArrayList<>();
}