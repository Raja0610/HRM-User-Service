package com.hrm.project.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Junction entity representing the association between an Organization and a User.
 *
 * <p>This entity is used to model a many-to-many relationship while allowing
 * additional attributes (such as active status) to be stored against the
 * association itself.</p>
 *
 * <p>The composite primary key consists of:
 * <ul>
 *     <li>organizationId</li>
 *     <li>userId</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "organization_users")
public class OrganizationUser {

    /**
     * Composite primary key containing organizationId and userId.
     */
    @EmbeddedId
    private OrganizationUserId id;

    /**
     * Associated organization.
     *
     * <p>Maps the organizationId field from the composite key and establishes
     * a many-to-one relationship with Organization.</p>
     */
    @MapsId("organizationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    /**
     * Associated user.
     *
     * <p>Maps the userId field from the composite key and establishes
     * a many-to-one relationship with User.</p>
     */
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Indicates whether the user is currently active within the organization.
     *
     * <p>Soft activation/deactivation can be managed through this flag
     * without removing the association record.</p>
     */
    @Column(nullable = false)
    private Boolean active = true;
}