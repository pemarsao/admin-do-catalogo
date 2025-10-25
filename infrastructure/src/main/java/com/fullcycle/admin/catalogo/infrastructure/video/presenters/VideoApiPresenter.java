package com.fullcycle.admin.catalogo.infrastructure.video.presenters;

import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaOutput;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.VideoOutput;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.VideosListOutput;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoOutput;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import com.fullcycle.admin.catalogo.infrastructure.video.models.AudioVideoMediaResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.ImageMediaResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UploadMediaResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideosListResponse;

public interface VideoApiPresenter {

    static VideoResponse present(final VideoOutput video) {
        return new VideoResponse(
            video.id(),
            video.title(),
            video.description(),
            video.launchAt(),
            video.duration(),
            video.opened(),
            video.published(),
            video.rating().getName(),
            video.createdAt(),
            video.updatedAt(),
            present(video.banner()),
            present(video.thumbnail()),
            present(video.thumbnailHalf()),
            present(video.video()),
            present(video.trailer()),
            video.categories(),
            video.genres(),
            video.castMembers()
        );
    }

    static AudioVideoMediaResponse present(final AudioVideoMedia media) {
        if (media == null) {
            return null;
        }
        return new AudioVideoMediaResponse(
            media.id(),
            media.checksum(),
            media.name(),
            media.rawLocation(),
            media.encodedLocation(),
            media.status().name()
        );
    }

    static ImageMediaResponse present(final ImageMedia image) {
        if (image == null) {
            return null;
        }

        return new ImageMediaResponse(
            image.id(),
            image.checksum(),
            image.name(),
            image.location()
        );
    }

    static UpdateVideoResponse present(UpdateVideoOutput output) {
        if (output == null) {
            return null;
        }
        return new UpdateVideoResponse(output.id());
    }

    static VideosListResponse present(VideosListOutput output) {
        if (output == null) {
            return null;
        }
        return new VideosListResponse(
            output.id(),
            output.title(),
            output.description(),
            output.createdAt(),
            output.updatedAt()
        );
    }

    static Pagination<VideosListResponse> present(Pagination<VideosListOutput> page) {
        return page.map(VideoApiPresenter::present);
    }

    static UploadMediaResponse present(UploadMediaOutput output) {
        return new UploadMediaResponse(
            output.videoId(),
            output.mediaType()
        );
    }
}
