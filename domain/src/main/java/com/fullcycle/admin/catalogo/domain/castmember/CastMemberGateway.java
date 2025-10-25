package com.fullcycle.admin.catalogo.domain.castmember;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;

public interface CastMemberGateway {

    CastMember create(CastMember aCastMember);

    CastMember update(CastMember aCastMember);

    void deleteById(CastMemberID anID);

    Optional<CastMember> findById(CastMemberID anID);

    Pagination<CastMember> findAll(SearchQuery aQuery);

    List<CastMemberID> existsByIds(Iterable<CastMemberID> ids);
}
