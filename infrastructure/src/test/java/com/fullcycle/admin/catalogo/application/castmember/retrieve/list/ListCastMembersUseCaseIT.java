package com.fullcycle.admin.catalogo.application.castmember.retrieve.list;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class ListCastMembersUseCaseIT {

    @Autowired
    private ListCastMembersUseCase useCase;
    @Autowired
    private CastMemberRepository castMemberRepository;
    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    public void givenAValidCommand_whenCallsListCastMembers_shouldReturnIt() {
        // given
        final var expectedCastMembers =
            List.of(
                CastMember.newMember(Fixture.name(), Fixture.CastMembers.type()),
                CastMember.newMember(Fixture.name(), Fixture.CastMembers.type())
            );

        this.castMemberRepository.saveAllAndFlush(expectedCastMembers.stream().map(
            CastMemberJpaEntity::from
        ).toList());

        Assertions.assertEquals(2, this.castMemberRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedItems = expectedCastMembers.stream()
            .map(CastMemberListOutput::from)
            .toList();

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertTrue(expectedItems.size() == actualOutput.items().size() &&
                              expectedItems.containsAll(actualOutput.items()));

        verify(castMemberGateway).findAll(any());
    }

    @Test
    public void givenAValidCommand_whenCallsListCastMembersWithEmptyData_shouldReturnEmpty() {
        // given
        final var expectedCastMembers =
            List.<CastMember>of(
            );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        Assertions.assertEquals(0, this.castMemberRepository.count());

        final var expectedItems = List.<CastMemberListOutput>of();

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(castMemberGateway).findAll(any());
    }

    @Test
    public void givenAnInvalidPage_whenCallsListCastMembers_shouldReturnError() {
        // given

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedMessage = "Gateway error";
        doThrow(new IllegalStateException(expectedMessage)).when(castMemberGateway).findAll(any());

        // when
        final var actualOutput = Assertions.assertThrows(
            IllegalStateException.class, () -> useCase.execute(aQuery)
        );

        // then
        Assertions.assertEquals(expectedMessage, actualOutput.getMessage());

        verify(castMemberGateway).findAll(any());
    }
}
