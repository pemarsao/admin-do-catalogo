package com.fullcycle.admin.catalogo.application.castmember.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DeleteCastMemberUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return null;
    }

    @Test
    void givenAValidCommand_whenCallsDeleteCastMember_shouldDeleteIt() {
        //given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = aMember.getId();

        doNothing().when(castMemberGateway).deleteById(eq(expectedId));

        //when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        //then
        verify(castMemberGateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    void givenAInvalidId_whenCallsDeleteCastMember_shouldOk() {
        //given
        final var expectedId = CastMemberID.from("123");

        doNothing().when(castMemberGateway).deleteById(eq(expectedId));

        //when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        //then
        verify(castMemberGateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnException() {
        //given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = aMember.getId();

        doThrow(new IllegalStateException("Gateway Error")).when(castMemberGateway).deleteById(eq(expectedId));

        //when
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        //then
        verify(castMemberGateway, times(1)).deleteById(eq(expectedId));
    }
}
