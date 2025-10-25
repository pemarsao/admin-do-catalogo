package com.fullcycle.admin.catalogo.domain.castmember;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;

public class CastMember extends AggregateRoot<CastMemberID> {

    private String name;
    private CastMemberType type;
    private Instant createdAt;
    private Instant updatedAt;

    private CastMember(final CastMemberID id, final String name, final CastMemberType type, final Instant createdAt, final Instant updatedAt) {
        super(id);
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        selfValidate();
    }

    public static CastMember newMember(
        final String name,
        final CastMemberType type
    ) {
        final var now = InstantUtils.now();
        return new CastMember(CastMemberID.unique(), name, type, now, now);
    }

    public static CastMember with(
        final CastMemberID id,
        final String name,
        final CastMemberType type,
        final Instant createdAt,
        final Instant updatedAt
    ) {
        return new CastMember(id, name, type, createdAt, updatedAt);
    }

    public static CastMember with(
        final CastMember castMember
    ) {
        return new CastMember(
            castMember.id,
            castMember.name,
            castMember.type,
            castMember.createdAt,
            castMember.updatedAt
        );
    }

    public CastMember update(
        final String aName,
        final CastMemberType aType
    ) {
        this.name = aName;
        this.type = aType;
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    public String getName() {
        return name;
    }

    public CastMemberType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void validate(ValidateHandler aHandler) {
        new CastMemberValidator(this, aHandler).validate();
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Failed to create a Aggregate CastMember", notification);
        }
    }
}
