package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.InternalErrorException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.MediaStatus;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.fullcycle.admin.catalogo.domain.video.VideoResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;
    @Mock
    private CategoryGateway categoryGateway;
    @Mock
    private GenreGateway genreGateway;
    @Mock
    private CastMemberGateway castMemberGateway;
    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, categoryGateway, genreGateway, castMemberGateway, mediaResourceGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideo_shouldReturnIdentifier() {
        // given
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
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        mockImageMedia();
        mockAudioVideoMedia();
        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(videoGateway).create(argThat(aVideo ->
            Objects.equals(expectedTitle, aVideo.getTitle())
             && Objects.equals(expectedDescription, aVideo.getDescription())
             && Objects.equals(expectedLaunchAt, aVideo.getLaunchedAt())
             && Objects.equals(expectedDuration, aVideo.getDuration())
             && Objects.equals(expectedRating, aVideo.getRating())
             && Objects.equals(expectedOpened, aVideo.getOpened())
             && Objects.equals(expectedPublished, aVideo.getPublished())
             && Objects.equals(expectedCategoriesIds, aVideo.getCategories())
             && Objects.equals(expectedCastMembersIds, aVideo.getCastMembers())
             && Objects.equals(expectedGenresIds, aVideo.getGenres())
             && Objects.equals(expectedVideo.name(), aVideo.getVideo().get().name())
             && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().get().name())
             && Objects.equals(expectedBanner.name(), aVideo.getBanner().get().name())
             && Objects.equals(expectedThumb.name(), aVideo.getThumbnail().get().name())
             && Objects.equals(expectedThumbHalf.name(), aVideo.getThumbnailHalf().get().name())
        ));
    }

    @Test
    public void givenAValidCommandWithoutCategories_whenCallsCreateVideo_shouldReturnIdentifier() {
        // given
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.<CategoryID>of();
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        mockImageMedia();
        mockAudioVideoMedia();
        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(videoGateway).create(argThat(aVideo ->
            Objects.equals(expectedTitle, aVideo.getTitle())
            && Objects.equals(expectedDescription, aVideo.getDescription())
            && Objects.equals(expectedLaunchAt, aVideo.getLaunchedAt())
            && Objects.equals(expectedDuration, aVideo.getDuration())
            && Objects.equals(expectedRating, aVideo.getRating())
            && Objects.equals(expectedOpened, aVideo.getOpened())
            && Objects.equals(expectedPublished, aVideo.getPublished())
            && Objects.equals(expectedCategoriesIds, aVideo.getCategories())
            && Objects.equals(expectedCastMembersIds, aVideo.getCastMembers())
            && Objects.equals(expectedGenresIds, aVideo.getGenres())
            && Objects.equals(expectedVideo.name(), aVideo.getVideo().get().name())
            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().get().name())
            && Objects.equals(expectedBanner.name(), aVideo.getBanner().get().name())
            && Objects.equals(expectedThumb.name(), aVideo.getThumbnail().get().name())
            && Objects.equals(expectedThumbHalf.name(), aVideo.getThumbnailHalf().get().name())
        ));
    }

    @Test
    public void givenAValidCommandWithoutCastMembers_whenCallsCreateVideo_shouldReturnIdentifier() {
        // given
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.<CastMemberID>of();
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));

        mockImageMedia();
        mockAudioVideoMedia();
        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(videoGateway).create(argThat(aVideo ->
            Objects.equals(expectedTitle, aVideo.getTitle())
            && Objects.equals(expectedDescription, aVideo.getDescription())
            && Objects.equals(expectedLaunchAt, aVideo.getLaunchedAt())
            && Objects.equals(expectedDuration, aVideo.getDuration())
            && Objects.equals(expectedRating, aVideo.getRating())
            && Objects.equals(expectedOpened, aVideo.getOpened())
            && Objects.equals(expectedPublished, aVideo.getPublished())
            && Objects.equals(expectedCategoriesIds, aVideo.getCategories())
            && Objects.equals(expectedCastMembersIds, aVideo.getCastMembers())
            && Objects.equals(expectedGenresIds, aVideo.getGenres())
            && Objects.equals(expectedVideo.name(), aVideo.getVideo().get().name())
            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().get().name())
            && Objects.equals(expectedBanner.name(), aVideo.getBanner().get().name())
            && Objects.equals(expectedThumb.name(), aVideo.getThumbnail().get().name())
            && Objects.equals(expectedThumbHalf.name(), aVideo.getThumbnailHalf().get().name())
        ));
    }

    @Test
    public void givenAValidCommandWithoutGenres_whenCallsCreateVideo_shouldReturnIdentifier() {
        // given
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.<GenreID>of();
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        mockImageMedia();
        mockAudioVideoMedia();
        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(videoGateway).create(argThat(aVideo ->
            Objects.equals(expectedTitle, aVideo.getTitle())
            && Objects.equals(expectedDescription, aVideo.getDescription())
            && Objects.equals(expectedLaunchAt, aVideo.getLaunchedAt())
            && Objects.equals(expectedDuration, aVideo.getDuration())
            && Objects.equals(expectedRating, aVideo.getRating())
            && Objects.equals(expectedOpened, aVideo.getOpened())
            && Objects.equals(expectedPublished, aVideo.getPublished())
            && Objects.equals(expectedCategoriesIds, aVideo.getCategories())
            && Objects.equals(expectedCastMembersIds, aVideo.getCastMembers())
            && Objects.equals(expectedGenresIds, aVideo.getGenres())
            && Objects.equals(expectedVideo.name(), aVideo.getVideo().get().name())
            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().get().name())
            && Objects.equals(expectedBanner.name(), aVideo.getBanner().get().name())
            && Objects.equals(expectedThumb.name(), aVideo.getThumbnail().get().name())
            && Objects.equals(expectedThumbHalf.name(), aVideo.getThumbnailHalf().get().name())
        ));
    }

    @Test
    public void givenAValidCommandWithoutResources_whenCallsCreateVideo_shouldReturnIdentifier() {
        // given
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
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumb = null;
        final Resource expectedThumbHalf = null;

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(videoGateway).create(argThat(aVideo ->
            Objects.equals(expectedTitle, aVideo.getTitle())
            && Objects.equals(expectedDescription, aVideo.getDescription())
            && Objects.equals(expectedLaunchAt, aVideo.getLaunchedAt())
            && Objects.equals(expectedDuration, aVideo.getDuration())
            && Objects.equals(expectedRating, aVideo.getRating())
            && Objects.equals(expectedOpened, aVideo.getOpened())
            && Objects.equals(expectedPublished, aVideo.getPublished())
            && Objects.equals(expectedCategoriesIds, aVideo.getCategories())
            && Objects.equals(expectedCastMembersIds, aVideo.getCastMembers())
            && Objects.equals(expectedGenresIds, aVideo.getGenres())
            && aVideo.getVideo().isEmpty()
            && aVideo.getTrailer().isEmpty()
            && aVideo.getBanner().isEmpty()
            && aVideo.getThumbnail().isEmpty()
            && aVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenANullTitle_whenCallsCreateVideo_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "'title' should not be null";
        final var expectedErrorCount = 1;
        final String expectedTitle = null;
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getErrors().get(0).message());

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenAEmptyTitle_whenCallsCreateVideo_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;
        final var expectedTitle = "";
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getErrors().get(0).message());

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenANullRating_whenCallsCreateVideo_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "'rating' should not be null";
        final var expectedErrorCount = 1;
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final String expectedRating = null;
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getErrors().get(0).message());

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenAInvalidRating_whenCallsCreateVideo_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "'rating' should not be null";
        final var expectedErrorCount = 1;
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final String expectedRating = "aaa";
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getErrors().get(0).message());

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenANullLaunchAt_whenCallsCreateVideo_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "'launchedAt' should not be null";
        final var expectedErrorCount = 1;
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final Integer expectedLaunchAt = null;
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(Fixture.Categories.category().getId());
        final var expectedCastMembersIds = Set.of(Fixture.CastMembers.castMember().getId());
        final var expectedGenresIds = Set.of(Fixture.Genres.genre().getId());
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt,
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getErrors().get(0).message());

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideoWithCategoriesNotFound_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "Some categories could not be found:";
        final var expectedErrorCount = 1;
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
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>());
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertTrue(actualOutput.getErrors().get(0).message().startsWith(expectedErrorMessage));

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideoWithGenresNotFound_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "Some genres could not be found:";
        final var expectedErrorCount = 1;
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
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>());
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertTrue(actualOutput.getErrors().get(0).message().startsWith(expectedErrorMessage));

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideoWithCastMembersNotFound_shouldReturnDomainException() {
        // given
        final var expectedErrorMessage = "Some cast members could not be found:";
        final var expectedErrorCount = 1;
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
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>());

        // when
        final var actualOutput = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorCount, actualOutput.getErrors().size());
        Assertions.assertTrue(actualOutput.getErrors().get(0).message().startsWith(expectedErrorMessage));

        verify(videoGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategoriesIds));
        verify(genreGateway, times(1)).existsByIds(eq(expectedGenresIds));
        verify(castMemberGateway, times(1)).existsByIds(eq(expectedCastMembersIds));
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeAudioVideo(any(), any());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideoThroughError_shouldClearStorage() {
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
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchAt.getValue(),
            expectedDuration,
            expectedRating.getName(),
            expectedOpened,
            expectedPublished,
            asString(expectedCategoriesIds),
            asString(expectedCastMembersIds),
            asString(expectedGenresIds),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumb,
            expectedThumbHalf
        );

        when(categoryGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCategoriesIds));
        when(genreGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedGenresIds));
        when(castMemberGateway.existsByIds(any())).thenReturn(new ArrayList<>(expectedCastMembersIds));

        mockImageMedia();
        mockAudioVideoMedia();
        when(videoGateway.create(any())).thenThrow(new RuntimeException("Internal server error"));

        // when
        final var actualOutput = Assertions.assertThrows(InternalErrorException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertTrue(actualOutput.getMessage().startsWith(expectedErrorMessage));

        verify(mediaResourceGateway, times(1)).clearResources(any());
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any())).thenAnswer(t -> {
            final var videoResource = t.getArgument(1, VideoResource.class);
            final var resource = videoResource.resource();
                return AudioVideoMedia.with(IdUtils.uuid(), IdUtils.uuid(), resource.name(), "/video/".concat(resource.name()), "", MediaStatus.PENDING);
            }
        );
    }

    private void mockImageMedia() {
        when(mediaResourceGateway.storeImage(any(), any())).thenAnswer(t -> {
                final var videoResource = t.getArgument(1, VideoResource.class);
                final var resource = videoResource.resource();
                return ImageMedia.with(resource.checksum(), resource.name(), "/video/".concat(resource.name()));
            }
        );

    }
}
