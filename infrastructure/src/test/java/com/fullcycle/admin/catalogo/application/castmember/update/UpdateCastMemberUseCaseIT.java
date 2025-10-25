package com.fullcycle.admin.catalogo.application.castmember.update;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class UpdateCastMemberUseCaseIT {


    @Autowired
    private UpdateCastMemberUseCase useCase;
    @Autowired
    private CastMemberRepository castMemberRepository;
    @SpyBean
    private CastMemberGateway castMemberGateway;


    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnSuccess() {
        // given
        final var aMember = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId().getValue();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        final var actualOutput = useCase.execute(aCommand);
        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        final var actualCastMember = castMemberRepository.findById(actualOutput.id()).get();

        Assertions.assertEquals(expectedId, actualCastMember.getId());
        Assertions.assertEquals(expectedName, actualCastMember.getName());
        Assertions.assertEquals(expectedType, actualCastMember.getType());
        Assertions.assertTrue(actualCastMember.getCreatedAt().isBefore(actualCastMember.getUpdatedAt()));

        verify(castMemberGateway, times(1)).update(any());
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCastMember_shouldThrowNotificationException() {
        // given
        final var aMember = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId().getValue();
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());

        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        verify(castMemberGateway, times(1)).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    public void givenAnInvalidType_whenCallsUpdateCastMember_shouldThrowNotificationException() {
        // given
        final var aMember = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId();
        final var expectedName = Fixture.name();
        final CastMemberType expectedType = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());

        final var aCommand = UpdateCastMemberCommand.with(expectedId.getValue(), expectedName, expectedType);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        verify(castMemberGateway, times(1)).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateCastMember_shouldThrowNotFoundException() {
        // given
        final var expectedId = "123";
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(CastMember.newMember(Fixture.name(), Fixture.CastMembers.type())));
        Assertions.assertEquals(1, castMemberRepository.count());

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        verify(castMemberGateway, times(1)).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }
}
