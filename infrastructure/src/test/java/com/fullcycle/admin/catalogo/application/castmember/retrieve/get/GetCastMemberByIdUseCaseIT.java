package com.fullcycle.admin.catalogo.application.castmember.retrieve.get;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class GetCastMemberByIdUseCaseIT {

    @Autowired
    private GetCastMemberByIdUseCase useCase;
    @Autowired
    private CastMemberRepository castMemberRepository;
    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    public void givenAValidId_whenCallsGetCastMemberById_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aCastMember = CastMember.newMember(expectedName, expectedType);

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember));
        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        final var actualOutput = useCase.execute(aCastMember.getId().getValue());

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(aCastMember.getId().getValue(), actualOutput.id());
        Assertions.assertEquals(expectedName, actualOutput.name());
        Assertions.assertEquals(expectedType, actualOutput.type());
        Assertions.assertNotNull(actualOutput.createdAt());
        Assertions.assertNotNull(actualOutput.updatedAt());

        verify(castMemberGateway).findById(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsGetCastMemberById_shouldReturnNotFoundException() {
        // given
        final var expectedId = CastMemberID.from("123");
        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        // when
        final var actualOutput = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        // then
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getMessage());
        verify(castMemberGateway).findById(expectedId);
    }
}
