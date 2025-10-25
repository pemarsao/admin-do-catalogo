package com.fullcycle.admin.catalogo.application.video.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.exceptions.InternalErrorException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class DeleteVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteVideoUseCase useCase;
    @Mock
    private VideoGateway videoGateway;
    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, mediaResourceGateway);
    }

    @Test
    public void givenAValidVideo_whenCallDeleteVideo_shouldBeOk() {
        // given
        final var expectedId = VideoID.unique();
        Mockito.doNothing().when(videoGateway).deleteById(expectedId);
        Mockito.doNothing().when(mediaResourceGateway).clearResources(expectedId);
        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        // then
        Mockito.verify(videoGateway, Mockito.times(1)).deleteById(expectedId);
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).clearResources(expectedId);
    }

    @Test
    public void givenAnInvalidVideo_whenCallDeleteVideo_shouldBeOk() {
        // given
        final var expectedId = VideoID.from("123");
        Mockito.doNothing().when(videoGateway).deleteById(expectedId);
        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        // then
        Mockito.verify(videoGateway, Mockito.times(1)).deleteById(expectedId);
    }

    @Test
    public void givenAValidVideo_whenCallDeleteVideoAndThrowsGateway_shouldException() {
        // given
        final var expectedId = VideoID.from("123");
        Mockito.doThrow(InternalErrorException.with("Gateway Error", new RuntimeException())).when(videoGateway).deleteById(expectedId);
        // when
        Assertions.assertThrows(InternalErrorException.class,() -> useCase.execute(expectedId.getValue()));
        // then
        Mockito.verify(videoGateway, Mockito.times(1)).deleteById(expectedId);
    }
}
