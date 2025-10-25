package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.domain.video.Video;

public record CreateVideoOutput(String id) {

    public static CreateVideoOutput from(final Video aVideo) {
        return new CreateVideoOutput(aVideo.getId().getValue());
    }

    public static CreateVideoOutput from(final String anId) {
        return new CreateVideoOutput(anId);
    }
}
