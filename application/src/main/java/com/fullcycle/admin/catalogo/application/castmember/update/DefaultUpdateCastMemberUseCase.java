package com.fullcycle.admin.catalogo.application.castmember.update;

import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public non-sealed class DefaultUpdateCastMemberUseCase extends UpdateCastMemberUseCase {

    final private CastMemberGateway castMemberGateway;

    public DefaultUpdateCastMemberUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public UpdateCastMemberOutput execute(UpdateCastMemberCommand anIn) {
        final var anId = CastMemberID.from(anIn.id());
        final var aName = anIn.name();
        final var aType = anIn.type();

        final var aCastMember = this.castMemberGateway.findById(anId).orElseThrow(notFound(anId));

        final var notification = Notification.create();
        notification.validate(() -> aCastMember.update(aName, aType));

        if (notification.hasError()) {
            notify(notification);
        }

        return UpdateCastMemberOutput.from(this.castMemberGateway.update(aCastMember));
    }

    private static void notify(Notification notification) {
        throw new NotificationException("Could not update Cast Member", notification);
    }

    private Supplier<? extends RuntimeException> notFound(CastMemberID anId) {
        return () -> NotFoundException.with(CastMember.class, anId);
    }
}
