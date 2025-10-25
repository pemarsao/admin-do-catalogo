package com.fullcycle.admin.catalogo.application.video.retrieve.get;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

import java.util.Objects;

public class DefaultGetVideoByIdUseCase extends GetVideoByIdUseCase {

    private final VideoGateway videoGateway;

    public DefaultGetVideoByIdUseCase(VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public VideoOutput execute(String anIn) {
        final var aVideoId = VideoID.from(anIn);
        return videoGateway.findById(aVideoId).map(VideoOutput::from).orElseThrow(
            () -> NotFoundException.with(Video.class, aVideoId)
        );
    }
}
