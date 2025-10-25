package com.fullcycle.admin.catalogo.infrastructure.api.controllers;

import com.fullcycle.admin.catalogo.application.video.create.CreateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.GetVideoByIdUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.ListVideosUseCase;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoUseCase;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.fullcycle.admin.catalogo.domain.video.VideoResource;
import com.fullcycle.admin.catalogo.domain.video.VideoSearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.api.VideoAPI;
import com.fullcycle.admin.catalogo.infrastructure.utils.HashingUtils;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideosListResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.presenters.VideoApiPresenter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

@RestController
public class VideoController implements VideoAPI {

    private final CreateVideoUseCase createVideoUseCase;
    private final GetVideoByIdUseCase getVideoByIdUseCase;
    private final UpdateVideoUseCase updateVideoUseCase;
    private final DeleteVideoUseCase deleteVideoUseCase;
    private final ListVideosUseCase listVideosUseCase;
    private final GetMediaUseCase getMediaUseCase;
    private final UploadMediaUseCase uploadMediaUseCase;

    public VideoController(
        final CreateVideoUseCase createVideoUseCase,
        final GetVideoByIdUseCase getVideoByIdUseCase,
        final UpdateVideoUseCase updateVideoUseCase,
        final DeleteVideoUseCase deleteVideoUseCase,
        final ListVideosUseCase listVideosUseCase,
        final GetMediaUseCase getMediaUseCase,
        final UploadMediaUseCase uploadMediaUseCase
    ) {
        this.createVideoUseCase = Objects.requireNonNull(createVideoUseCase);
        this.getVideoByIdUseCase = Objects.requireNonNull(getVideoByIdUseCase);
        this.updateVideoUseCase = Objects.requireNonNull(updateVideoUseCase);
        this.deleteVideoUseCase = Objects.requireNonNull(deleteVideoUseCase);
        this.listVideosUseCase = Objects.requireNonNull(listVideosUseCase);
        this.getMediaUseCase = Objects.requireNonNull(getMediaUseCase);
        this.uploadMediaUseCase = Objects.requireNonNull(uploadMediaUseCase);
    }

    @Override
    public ResponseEntity<?> createFull(final String aTitle,
                                        final String aDescription,
                                        final Integer launchAt,
                                        final Double aDuration,
                                        final Boolean wasOpened,
                                        final Boolean wasPublished,
                                        final String rating,
                                        final Set<String> categories,
                                        final Set<String> members,
                                        final Set<String> genres,
                                        final MultipartFile videoFile,
                                        final MultipartFile trailerFile,
                                        final MultipartFile bannerFile,
                                        final MultipartFile thumbFile,
                                        final MultipartFile thumbHalfFile) {

        final var aCmd = CreateVideoCommand.with(
            aTitle,
            aDescription,
            launchAt,
            aDuration,
            rating,
            wasOpened,
            wasPublished,
            categories,
            members,
            genres,
            resourceOf(videoFile),
            resourceOf(trailerFile),
            resourceOf(bannerFile),
            resourceOf(thumbFile),
            resourceOf(thumbHalfFile)
        );
        final var output = this.createVideoUseCase.execute(aCmd);
        return ResponseEntity.created(URI.create("/videos/" + output.id()))
            .body(output);
    }

    @Override
    public ResponseEntity<?> createPart(final CreateVideoRequest createVideoRequest) {
        final var aCmd = CreateVideoCommand.with(
            createVideoRequest.title(),
            createVideoRequest.description(),
            createVideoRequest.launchYear(),
            createVideoRequest.duration(),
            createVideoRequest.rating(),
            createVideoRequest.opened(),
            createVideoRequest.published(),
            createVideoRequest.categoriesId(),
            createVideoRequest.castMembersId(),
            createVideoRequest.genresId()
        );
        final var output = this.createVideoUseCase.execute(aCmd);
        return ResponseEntity.created(URI.create("/videos/" + output.id()))
            .body(output);
    }

    @Override
    public VideoResponse getVideoById(String anId) {
        return VideoApiPresenter.present(this.getVideoByIdUseCase.execute(anId));
    }

    @Override
    public ResponseEntity<?> update(final String anId, final UpdateVideoRequest payload) {
        final var aCmd = UpdateVideoCommand.with(
            anId,
            payload.title(),
            payload.description(),
            payload.launchYear(),
            payload.duration(),
            payload.rating(),
            payload.opened(),
            payload.published(),
            payload.categoriesId(),
            payload.castMembersId(),
            payload.genresId()
        );
        final var output = VideoApiPresenter.present(this.updateVideoUseCase.execute(aCmd));
        return ResponseEntity
            .ok()
            .location(URI.create("/videos/" + output.id()))
            .body(output);
    }

    @Override
    public void deleteById(String anId) {
        this.deleteVideoUseCase.execute(anId);
    }

    @Override
    public Pagination<VideosListResponse> listVideos(final String search,
                                                     final int page,
                                                     final int perPage,
                                                     final String sort,
                                                     final String direction,
                                                     final Set<String> castMembers,
                                                     final Set<String> categories,
                                                     final Set<String> genres) {
        final var categoriesIDs = CollectionUtils.mapTo(categories, CategoryID::from);
        final var genresIDs = CollectionUtils.mapTo(genres, GenreID::from);
        final var castMembersIDs = CollectionUtils.mapTo(castMembers, CastMemberID::from);
        final var aQuery = new VideoSearchQuery(page, perPage, search, sort, direction, castMembersIDs, categoriesIDs, genresIDs);

        return VideoApiPresenter.present(listVideosUseCase.execute(aQuery));
    }

    @Override
    public ResponseEntity<byte[]> getMediaByType(String anId, String type) {
        final var aMedia = getMediaUseCase.execute(GetMediaCommand.with(anId, type));

        return ResponseEntity.ok()
            .contentType(MediaType.valueOf(aMedia.contentType()))
            .contentLength(aMedia.content().length)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(aMedia.name()))
            .body(aMedia.content());
    }

    @Override
    public ResponseEntity<?> uploadMediaByType(final String anId, final String type, final MultipartFile media) {
        final var mediaType = VideoMediaType.of(type)
            .orElseThrow(() -> NotificationException.with(new Error("Media type %s is not supported".formatted(type))));
        final var aCmd = UploadMediaCommand.with(anId, VideoResource.with(resourceOf(media), mediaType));
        final var output = this.uploadMediaUseCase.execute(aCmd);
        return ResponseEntity.created(URI.create("/videos/%s/medias/%s".formatted(anId, type)))
            .body(VideoApiPresenter.present(output));
    }

    private Resource resourceOf(MultipartFile part) {
        if (part == null) {
            return null;
        }
        try {
            return Resource.with(
                HashingUtils.checksum(part.getBytes()),
                part.getBytes(),
                part.getContentType(),
                part.getOriginalFilename()
            );
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
