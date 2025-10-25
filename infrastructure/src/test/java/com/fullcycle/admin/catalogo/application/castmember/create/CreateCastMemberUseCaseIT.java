package com.fullcycle.admin.catalogo.application.castmember.create;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class CreateCastMemberUseCaseIT {

    @Autowired
    private CreateCastMemberUseCase useCase;
    @Autowired
    private CastMemberRepository castMemberRepository;
    @SpyBean
    private CastMemberGateway castMemberGateway;


    @Test
    public void givenAValidCommand_whenCallCreateCastMember_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualCastMember = castMemberRepository.findById(actualOutput.id()).get();
        Assertions.assertEquals(expectedName, actualCastMember.getName());
        Assertions.assertEquals(expectedType, actualCastMember.getType());
        Assertions.assertNotNull(actualCastMember.getCreatedAt());
        Assertions.assertNotNull(actualCastMember.getUpdatedAt());
        Assertions.assertEquals(actualCastMember.getCreatedAt(), actualCastMember.getUpdatedAt());

        verify(castMemberGateway).create(any());
    }

    @Test
    public void givenAInvalidName_whenCallsCreateCastMember_thenShouldReturnNotificationsException() {
        // given
        final String expectedName = null;
        final var expectedType = Fixture.CastMembers.type();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        // when
        final var actualError = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualError);
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

        verify(castMemberGateway, times(0)).create(any());
    }

    @Test
    public void givenAInvalidType_whenCallsCreateCastMember_thenShouldReturnNotificationsException() {
        // given
        final var expectedName = Fixture.name();
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        // when
        final var actualError = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualError);
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());

        verify(castMemberGateway, times(0)).create(any());
    }
}
