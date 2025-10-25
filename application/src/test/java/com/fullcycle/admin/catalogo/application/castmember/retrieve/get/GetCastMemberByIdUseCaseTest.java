package com.fullcycle.admin.catalogo.application.castmember.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetCastMemberByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetCastMemberByIdUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetCastMemberById_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aCastMember = CastMember.newMember(expectedName, expectedType);

        when(castMemberGateway.findById(any())).thenReturn(Optional.of(aCastMember));

        // when
        final var actualOutput = useCase.execute(aCastMember.getId().getValue());

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(aCastMember.getId().getValue(), actualOutput.id());
        Assertions.assertEquals(expectedName, actualOutput.name());
        Assertions.assertEquals(expectedType, actualOutput.type());
        Assertions.assertEquals(aCastMember.getCreatedAt(), actualOutput.createdAt());
        Assertions.assertEquals(aCastMember.getUpdatedAt(), actualOutput.updatedAt());

        verify(castMemberGateway).findById(aCastMember.getId());
    }

    @Test
    public void givenAnInvalidId_whenCallsGetCastMemberById_shouldReturnNotFoundException() {
        // given
        final var expectedId = CastMemberID.from("123");
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        when(castMemberGateway.findById(any())).thenReturn(Optional.empty());

        // when
        final var actualOutput = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        // then
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getMessage());
        verify(castMemberGateway).findById(expectedId);
    }
}
