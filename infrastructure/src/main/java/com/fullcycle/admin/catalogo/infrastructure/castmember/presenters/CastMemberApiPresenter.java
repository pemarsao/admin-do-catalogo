package com.fullcycle.admin.catalogo.infrastructure.castmember.presenters;

import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.GetCastMemberOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;

public interface CastMemberApiPresenter {

    static CastMemberResponse present(GetCastMemberOutput output) {
        return new CastMemberResponse(
            output.id(),
            output.name(),
            output.type().name(),
            output.createdAt().toString(),
            output.updatedAt().toString()
        );
    }

    static CastMemberListResponse present(CastMemberListOutput output) {
        return new CastMemberListResponse(
            output.id(),
            output.name(),
            output.type().name(),
            output.createdAt().toString()
        );
    }

}
