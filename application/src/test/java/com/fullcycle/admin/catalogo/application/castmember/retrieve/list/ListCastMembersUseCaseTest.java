package com.fullcycle.admin.catalogo.application.castmember.retrieve.list;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListCastMembersUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultListCastMembersUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsListCastMembers_shouldReturnIt() {
        // given
        final var expectedCastMembers =
            List.of(
                CastMember.newMember(Fixture.name(), Fixture.CastMembers.type()),
                CastMember.newMember(Fixture.name(), Fixture.CastMembers.type())
            );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedItems = expectedCastMembers.stream()
            .map(CastMemberListOutput::from)
            .toList();

        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedCastMembers);

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        when(castMemberGateway.findAll(any())).thenReturn(expectedPagination);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(castMemberGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidCommand_whenCallsListCastMembersWithEmptyData_shouldReturnEmpty() {
        // given
        final var expectedCastMembers =
            List.<CastMember>of(
            );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems = List.<CastMemberListOutput>of();

        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedCastMembers);

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        when(castMemberGateway.findAll(any())).thenReturn(expectedPagination);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(castMemberGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAnInvalidPage_whenCallsListCastMembers_shouldReturnError() {
        // given

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedMessage = "Gateway error";
        when(castMemberGateway.findAll(any())).thenThrow(new IllegalStateException(expectedMessage));

        // when
        final var actualOutput = Assertions.assertThrows(
            IllegalStateException.class, () -> useCase.execute(aQuery)
        );

        // then
        Assertions.assertEquals(expectedMessage, actualOutput.getMessage());

        verify(castMemberGateway).findAll(eq(aQuery));
    }
}
