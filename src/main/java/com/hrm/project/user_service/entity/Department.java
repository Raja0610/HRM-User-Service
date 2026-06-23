package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a department within a branch.
 *
 * <p>
 * Examples:
 * <ul>
 *     <li>Human Resources</li>
 *     <li>Engineering</li>
 *     <li>Finance</li>
 *     <li>Operations</li>
 * </ul>
 * </p>
 *
 * <p>
 * A department belongs to exactly one branch,
 * while a branch can contain multiple departments.
 * </p>
 */
@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
public class Department {

    /**
     * Unique identifier of the department.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Internal department name.
     * <p>
     * Example:
     * HR
     * Engineering
     * Finance
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * User-friendly display name.
     * <p>
     * Example:
     * Human Resources
     * Software Engineering
     */
    @Column(nullable = false, length = 150)
    private String displayName;

    /**
     * Indicates whether the department is active.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Workforce count or workforce information.
     */
    @Column
    private Long workforce=0L;

    /**
     * Dynamic custom attributes.
     * <p>
     * Example:
     * {
     * "shiftType": "Day",
     * "costCenter": "CC-1001"
     * }
     */
    @Column(columnDefinition = "TEXT")
    private String specialAttributes;

    /**
     * Branch to which this department belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
}