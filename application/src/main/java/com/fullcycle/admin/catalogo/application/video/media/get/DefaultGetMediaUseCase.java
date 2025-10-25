package com.fullcycle.admin.catalogo.application.video.media.get;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;

import java.util.Objects;

public class DefaultGetMediaUseCase extends GetMediaUseCase{

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public MediaOutput execute(GetMediaCommand aCmd) {
        final var anId = VideoID.from(aCmd.videoId());
        final var aType = VideoMediaType.of(aCmd.mediaType())
            .orElseThrow(() -> notFound(aCmd.mediaType()));
        final var aResource = this.mediaResourceGateway.getResource(anId, aType)
            .orElseThrow(() -> notFound(aCmd.videoId(), aCmd.mediaType()));
        return MediaOutput.with(aResource);
    }

    private NotFoundException notFound(String videoId, String mediaType) {
        return NotFoundException.with(new Error("Resource %s not found for video %s".formatted(videoId, mediaType)));
    }

    private NotFoundException notFound(String mediaType) {
        return NotFoundException.with(new Error("Media type %s doesn't exists".formatted(mediaType)));
    }
}
