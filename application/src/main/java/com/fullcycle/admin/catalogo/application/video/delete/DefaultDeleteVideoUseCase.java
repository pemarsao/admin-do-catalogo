package com.fullcycle.admin.catalogo.application.video.delete;

import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

import java.util.Objects;

public class DefaultDeleteVideoUseCase extends DeleteVideoUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultDeleteVideoUseCase(
        final VideoGateway videoGateway,
        final MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }


    @Override
    public void execute(String anIn) {
        final var aId = VideoID.from(anIn);
        this.videoGateway.deleteById(aId);
        this.mediaResourceGateway.clearResources(aId);
    }
}
