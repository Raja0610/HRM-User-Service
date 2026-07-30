package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * An access role defined by an organization, such as Administrator or HR Manager.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    /** Unique internal role identifier, for example HR_MANAGER. */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** User-facing role label. It may be shared by multiple roles. */
    @Column(nullable = false, length =100)
    private String displayName;

    @Column(length = 500, columnDefinition = "TEXT")
    private String description;

    /** The organization that owns and defines this role. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /** Authorities granted when this role is assigned to an organization user. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_authorities",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id")
    )
    private Set<Authority> authorities = new HashSet<>();

    /** Organization memberships that have this role. */
    @ManyToMany(mappedBy = "roles")
    private Set<OrganizationUser> organizationUsers = new HashSet<>();
}
