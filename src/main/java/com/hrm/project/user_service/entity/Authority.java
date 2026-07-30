package com.hrm.project.user_service.entity;

import com.hrm.project.user_service.enums.AuthorityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** A permission granted to roles in the RBAC model. */
@Entity
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 100)
    private AuthorityType name;

    @Column(nullable = false, length = 255)
    private String description;

    /** Roles that grant this authority. */
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles = new HashSet<>();
}
