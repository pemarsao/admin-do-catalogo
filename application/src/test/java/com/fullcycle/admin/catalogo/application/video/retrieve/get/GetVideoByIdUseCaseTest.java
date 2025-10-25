package com.fullcycle.admin.catalogo.application.video.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import com.fullcycle.admin.catalogo.domain.video.MediaStatus;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetVideoByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetVideoByIdUseCase useCase;

    @Mock
    private VideoGateway videoGateway;
    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    public void givenAValidVideoId_whenCallsGetVideoById_shouldReturnVideo() {
        // given
        final var expectedErrorMessage = "An error occurred while creating video [VideoID:";
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final var expectedVideo = audioVideoMedia(VideoMediaType.VIDEO);
        final var expectedTrailer = audioVideoMedia(VideoMediaType.TRAILER);
        final var expectedBanner = imageMedia(VideoMediaType.BANNER);
        final var expectedThumb = imageMedia(VideoMediaType.THUMBNAIL);
        final var expectedThumbHalf = imageMedia(VideoMediaType.THUMBNAIL_HALF);

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategoriesIds,
            expectedGenresIds,
            expectedCastMembersIds
        ).updateVideoMedia(expectedVideo)
            .updateTrailerMedia(expectedTrailer)
            .updateBannerMedia(expectedBanner)
            .updateThumbnailMedia(expectedThumb).updateThumbnailHalfMedia(expectedThumbHalf);

        final var expectedVideoId = aVideo.getId();
        when(videoGateway.findById(any())).thenReturn(Optional.of(aVideo));

        // when
        final var actualVideo = useCase.execute(expectedVideoId.getValue());

        // then
        Assertions.assertEquals(actualVideo.id(), expectedVideoId.getValue());
        Assertions.assertEquals(actualVideo.title(),expectedTitle);
        Assertions.assertEquals(actualVideo.description(),expectedDescription);
        Assertions.assertEquals(actualVideo.launchAt(),expectedLaunchAt.getValue());
        Assertions.assertEquals(actualVideo.duration(),expectedDuration);
        Assertions.assertEquals(actualVideo.rating(),expectedRating);
        Assertions.assertEquals(actualVideo.opened(),expectedOpened);
        Assertions.assertEquals(actualVideo.published(),expectedPublished);
        Assertions.assertEquals(actualVideo.categories(), asString(expectedCategoriesIds));
        Assertions.assertEquals(actualVideo.genres(), asString(expectedGenresIds));
        Assertions.assertEquals(actualVideo.castMembers(), asString(expectedCastMembersIds));
        Assertions.assertEquals(actualVideo.video(), expectedVideo);
        Assertions.assertEquals(actualVideo.trailer(), expectedTrailer);
        Assertions.assertEquals(actualVideo.banner(), expectedBanner);
        Assertions.assertEquals(actualVideo.thumbnail(), expectedThumb);
        Assertions.assertEquals(actualVideo.thumbnailHalf(), expectedThumbHalf);
        Assertions.assertEquals(actualVideo.createdAt(), aVideo.getCreatedAt());
        Assertions.assertEquals(actualVideo.updatedAt(), aVideo.getUpdatedAt());
        verify(videoGateway, times(1)).findById(expectedVideoId);
    }

    @Test
    public void givenAInvalidVideoId_whenCallsGetVideoById_shouldThrowNotFoundException() {
        // given
        final var expectedVideoId = VideoID.from("123");
        final var expectedErrorMessage = "Video with ID 123 was not found";
        when(videoGateway.findById(any())).thenReturn(Optional.empty());

        // when
        final var actualException = Assertions.assertThrows(
            NotFoundException.class,
            () -> useCase.execute(expectedVideoId.getValue())
        );

        // then
        Assertions.assertEquals(actualException.getMessage(), expectedErrorMessage);
        verify(videoGateway, times(1)).findById(expectedVideoId);
    }

    private AudioVideoMedia audioVideoMedia(final VideoMediaType type) {
        String checksum = IdUtils.uuid();
        return AudioVideoMedia.with(
            checksum,
            checksum,
            type.name().toLowerCase(),
            "/videos/" + checksum,
            "",
            MediaStatus.PENDING
        );
    }
    private ImageMedia imageMedia(final VideoMediaType type) {
        String checksum = IdUtils.uuid();
        return ImageMedia.with(
            checksum,
            type.name().toLowerCase(),
            "/images/" + checksum
        );
    }

}
