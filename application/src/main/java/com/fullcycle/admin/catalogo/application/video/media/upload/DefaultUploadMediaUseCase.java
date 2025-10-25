package com.fullcycle.admin.catalogo.application.video.media.upload;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

import java.util.Objects;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final VideoGateway videoGateway;

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUploadMediaUseCase(final VideoGateway videoGateway, final MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public UploadMediaOutput execute(UploadMediaCommand aCmd) {
        final var videoId = VideoID.from(aCmd.videoId());
        final var videoResource = aCmd.videoResource();

        final var aVideo = this.videoGateway.findById(videoId).orElseThrow(() -> notFound(videoId));

        switch (videoResource.type()) {
            case VIDEO -> aVideo.updateVideoMedia(mediaResourceGateway.storeAudioVideo(videoId, videoResource));
            case TRAILER -> aVideo.updateTrailerMedia(mediaResourceGateway.storeAudioVideo(videoId, videoResource));
            case BANNER -> aVideo.updateBannerMedia(mediaResourceGateway.storeImage(videoId, videoResource));
            case THUMBNAIL -> aVideo.updateThumbnailMedia(mediaResourceGateway.storeImage(videoId, videoResource));
            case THUMBNAIL_HALF -> aVideo.updateThumbnailHalfMedia(mediaResourceGateway.storeImage(videoId, videoResource));
        }

        return UploadMediaOutput.with(this.videoGateway.update(aVideo), videoResource.type());
    }

    private NotFoundException notFound(final VideoID aVideo) {
        return NotFoundException.with(Video.class, aVideo);
    }
}
