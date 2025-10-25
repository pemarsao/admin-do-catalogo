package com.fullcycle.admin.catalogo.application.castmember.delete;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class DeleteCastMemberUseCaseIT {

    @Autowired
    DeleteCastMemberUseCase useCase;
    @Autowired
    CastMemberRepository castMemberRepository;
    @SpyBean
    CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCommand_whenCallsDeleteCastMember_shouldDeleteIt() {
        //given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var aMemberTwo = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = aMember.getId();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMemberTwo));
        Assertions.assertEquals(2, castMemberRepository.count());

        //when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        //then
        Assertions.assertEquals(1, castMemberRepository.count());
        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAInvalidId_whenCallsDeleteCastMember_shouldOk() {
        //given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var aMemberTwo = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = CastMemberID.from("123");

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMemberTwo));
        Assertions.assertEquals(2, castMemberRepository.count());

        //when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        //then
        Assertions.assertEquals(2, castMemberRepository.count());
        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsException_shouldReturnException() {
        //given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var aMemberTwo = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = aMember.getId();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMemberTwo));
        Assertions.assertEquals(2, castMemberRepository.count());

        doThrow(new IllegalStateException("Gateway Error")).when(castMemberGateway).deleteById(eq(expectedId));

        //when
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        //then
        Assertions.assertEquals(2, castMemberRepository.count());
        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }
}
