package com.hrm.project.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite primary key for the OrganizationUser entity.
 *
 * <p>Represents the unique association between an Organization and a User.
 * The combination of organizationId and userId ensures that a user can have
 * only one membership record within a specific organization.</p>
 *
 * <p>This class is embedded into OrganizationUser using {@code @EmbeddedId}.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class OrganizationUserId implements Serializable {

    /**
     * Identifier of the associated organization.
     */
    @Column(name = "organization_id")
    private UUID organizationId;

    /**
     * Identifier of the associated user.
     */
    @Column(name = "user_id")
    private UUID userId;
}