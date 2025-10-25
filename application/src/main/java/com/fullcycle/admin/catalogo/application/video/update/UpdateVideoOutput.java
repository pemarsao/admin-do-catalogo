package com.fullcycle.admin.catalogo.application.video.update;

import com.fullcycle.admin.catalogo.domain.video.Video;

public record UpdateVideoOutput(String id) {

    public static UpdateVideoOutput from(final Video aVideo) {
        return new UpdateVideoOutput(aVideo.getId().getValue());
    }

    public static UpdateVideoOutput from(final String anId) {
        return new UpdateVideoOutput(anId);
    }
}
