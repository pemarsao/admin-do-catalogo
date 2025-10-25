package com.fullcycle.admin.catalogo.application.castmember.retrieve.get;

import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;

import java.util.Objects;

public non-sealed class DefaultGetCastMemberByIdUseCase extends GetCastMemberByIdUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultGetCastMemberByIdUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public GetCastMemberOutput execute(String anIn) {
        CastMemberID anCastMemberId = CastMemberID.from(anIn);
            return this.castMemberGateway.findById(anCastMemberId)
                .map(GetCastMemberOutput::from)
                .orElseThrow(() -> NotFoundException.with(CastMember.class, anCastMemberId));
    }
}
