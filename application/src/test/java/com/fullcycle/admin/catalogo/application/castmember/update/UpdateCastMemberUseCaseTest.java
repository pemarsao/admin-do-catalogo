package com.fullcycle.admin.catalogo.application.castmember.update;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateCastMemberUseCaseTest extends UseCaseTest {


    @InjectMocks
    private DefaultUpdateCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnSuccess() {
        // given
        final var aMember = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId().getValue();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(CastMember.with(aMember)));

        when(castMemberGateway.update(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        verify(castMemberGateway, times(1)).findById(any());
        verify(castMemberGateway, times(1)).update(argThat(aUpdateCastMember ->
            Objects.equals(expectedId, aUpdateCastMember.getId().getValue()) &&
            Objects.equals(expectedName, aUpdateCastMember.getName()) &&
            Objects.equals(expectedType, aUpdateCastMember.getType()) &&
            Objects.nonNull(aUpdateCastMember.getCreatedAt()) &&
            Objects.nonNull(aUpdateCastMember.getUpdatedAt())
        ));
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

        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);

        when(castMemberGateway.findById(any()))
            .thenReturn(Optional.of(aMember));

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

        final var aCommand = UpdateCastMemberCommand.with(expectedId.getValue(), expectedName, expectedType);

        when(castMemberGateway.findById(any()))
            .thenReturn(Optional.of(aMember));

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

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);

        when(castMemberGateway.findById(any()))
            .thenReturn(Optional.empty());

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        verify(castMemberGateway, times(1)).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }
}
