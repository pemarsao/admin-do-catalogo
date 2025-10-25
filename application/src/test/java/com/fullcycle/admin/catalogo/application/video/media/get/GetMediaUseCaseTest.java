package com.fullcycle.admin.catalogo.application.video.media.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class GetMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetMediaUseCase useCase;

    @Mock
    private MediaResourceGateway gateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(gateway);
    }

    @Test
    public void givenVideoIdAndType_whenIsValidCmd_shouldReturnResources() {
        //given
        final var expectedId = VideoID.unique();
        final var expectedType = Fixture.Videos.mediaType();
        final var expectedResource = Fixture.Videos.resource(expectedType);

        Mockito.when(gateway.getResource(expectedId, expectedType)).thenReturn(Optional.of(expectedResource));

        final var aCmd = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        //when
        final var actualResult = this.useCase.execute(aCmd);

        //then
        Assertions.assertEquals(expectedResource.name(), actualResult.name());
        Assertions.assertEquals(expectedResource.contentType(), actualResult.contentType());
        Assertions.assertEquals(expectedResource.content(), actualResult.content());
    }

    @Test
    public void givenVideoIdAndType_whenIsNotFound_shouldReturnNotFoundException() {
        //given
        final var expectedId = VideoID.unique();
        final var expectedType = Fixture.Videos.mediaType();
        final var expectedErrorMessage = "Resource %s not found for video %s".formatted(expectedId.getValue(), expectedType.name());

        Mockito.when(gateway.getResource(expectedId, expectedType)).thenReturn(Optional.empty());

        final var aCmd = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        //when
        final var actualError = Assertions.assertThrows(NotFoundException.class, () -> {
            this.useCase.execute(aCmd);
        });

        //then
        Assertions.assertEquals(expectedErrorMessage, actualError.getMessage());

    }

    @Test
    public void givenVideoIdAndType_whenIsNotFoundType_shouldReturnNotFoundException() {
        //given
        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Media type QUALQUER COISA doesn't exists";

        final var aCmd = GetMediaCommand.with(expectedId.getValue(), "QUALQUER COISA");

        //when
        final var actualError = Assertions.assertThrows(NotFoundException.class, () -> {
            this.useCase.execute(aCmd);
        });

        //then
        Assertions.assertEquals(expectedErrorMessage, actualError.getMessage());

    }

}
