package com.fullcycle.admin.catalogo.application.video.media.update;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.MediaStatus;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;

import java.util.Objects;

import static com.fullcycle.admin.catalogo.domain.video.VideoMediaType.*;

public class DefaultUpdateMediaStatusUseCase extends UpdateMediaStatusUseCase {

    private final VideoGateway videoGateway;

    public DefaultUpdateMediaStatusUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(UpdateMediaStatusCommand aCmd) {
        final var status = aCmd.status();
        final var videoId = VideoID.from(aCmd.videoId());
        final var resourceId = aCmd.resourceId();
        final var folder = aCmd.folder();
        final var filename = aCmd.filename();

        final var aVideo = this.videoGateway.findById(videoId).orElseThrow(
            () -> notFound(videoId)
        );

        final var encodedPath = "%s/%s".formatted(folder, filename);

        if (matches(resourceId, aVideo.getVideo().orElse(null))) {
            update(VIDEO, aCmd.status(), aVideo, encodedPath);
        } else if (matches(resourceId, aVideo.getTrailer().orElse(null))) {
            update(TRAILER, aCmd.status(), aVideo, encodedPath);
        }
    }

    private void update(final VideoMediaType aType, final MediaStatus aStatus, final Video aVideo, final String encodedPath) {
        switch (aStatus) {
            case PENDING -> {}
            case PROCESSING -> aVideo.processing(aType);
            case COMPLETED -> aVideo.completed(aType, encodedPath);
        }
        this.videoGateway.update(aVideo);
    }

    private boolean matches(String resourceId, AudioVideoMedia aMedia) {
        if (aMedia == null) {
            return false;
        }
        return aMedia.id().equals(resourceId);
    }

    private NotFoundException notFound(VideoID aId) {
        return NotFoundException.with(Video.class, aId);
    }
}
