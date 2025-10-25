package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

public class VideoTest extends UnitTest {

    @Test
    public void givenValidParameters_whenCallNewVideo_thenInstantiateVideo() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        // when
        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());
        Assertions.assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertNotNull(actualVideo.getUpdatedAt());
        Assertions.assertEquals(actualVideo.getCreatedAt(), actualVideo.getUpdatedAt());
        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.getOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.getPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenre, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertTrue(actualVideo.getVideo().isEmpty());
        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());
        Assertions.assertTrue(actualVideo.getBanner().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnail().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnailHalf().isEmpty());
        Assertions.assertTrue(actualVideo.getDomainEvents().isEmpty());

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));

    }

    @Test
    public void givenValidParameters_whenCallUpdateVideo_thenInstantiateVideo() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var aVideo = Video.newVideo(
            "Shazam",
            "sssss",
            Year.of(1998),
            0.0,
            Rating.AGE_10,
            true,
            true,
            Set.of(),
            Set.of(),
            Set.of()
        );
        // when
        final var actualVideo = Video.with(aVideo).update(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());
        Assertions.assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertNotNull(actualVideo.getUpdatedAt());
        Assertions.assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
        Assertions.assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.getOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.getPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenre, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertTrue(actualVideo.getVideo().isEmpty());
        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());
        Assertions.assertTrue(actualVideo.getBanner().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnail().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));

    }

    @Test
    public void givenValidParameters_whenCallSetVideo_thenInstantiateVideo() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedDomainEventSize = 1;

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        final var aVideoMedia = AudioVideoMedia.with("abc", "checksum", "Video.mp4", "/132/video", "", MediaStatus.PENDING);
        // when
        final var actualVideo = Video.with(aVideo).updateVideoMedia(aVideoMedia);

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());
        Assertions.assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertNotNull(actualVideo.getUpdatedAt());
        Assertions.assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
        Assertions.assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.getOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.getPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenre, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertEquals(actualVideo.getVideo().get(), aVideoMedia);
        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());
        Assertions.assertTrue(actualVideo.getBanner().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnail().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        Assertions.assertEquals(expectedDomainEventSize, actualVideo.getDomainEvents().size());

        final var actualEvent = (VideoMediaCreated) actualVideo.getDomainEvents().get(0);
        Assertions.assertEquals(aVideo.getId().getValue(), actualEvent.resourceId());
        Assertions.assertEquals(aVideoMedia.rawLocation(), actualEvent.filePath());
        Assertions.assertNotNull(actualEvent.occurredOn());

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));

    }

    @Test
    public void givenValidParameters_whenCallSetTrailer_thenInstantiateVideo() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedDomainEventSize = 1;

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        final var aVideoMedia = AudioVideoMedia.with("abc", "checksum" ,"Trailer.mp4", "/132/video", "", MediaStatus.PENDING);
        // when
        final var actualVideo = Video.with(aVideo).updateTrailerMedia(aVideoMedia);

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());
        Assertions.assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertNotNull(actualVideo.getUpdatedAt());
        Assertions.assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
        Assertions.assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.getOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.getPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenre, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertTrue(actualVideo.getVideo().isEmpty());
        Assertions.assertEquals(actualVideo.getTrailer().get(), aVideoMedia);
        Assertions.assertTrue(actualVideo.getBanner().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnail().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        Assertions.assertEquals(expectedDomainEventSize, actualVideo.getDomainEvents().size());

        final var actualEvent = (VideoMediaCreated) actualVideo.getDomainEvents().get(0);
        Assertions.assertEquals(aVideo.getId().getValue(), actualEvent.resourceId());
        Assertions.assertEquals(aVideoMedia.rawLocation(), actualEvent.filePath());
        Assertions.assertNotNull(actualEvent.occurredOn());

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));

    }

    @Test
    public void givenValidParameters_whenCallSetBanner_thenInstantiateVideo() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        final var aImageVideo = ImageMedia.with("abc", "Banner.jpg", "/132/video");
        // when
        final var actualVideo = Video.with(aVideo).updateBannerMedia(aImageVideo);

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());
        Assertions.assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertNotNull(actualVideo.getUpdatedAt());
        Assertions.assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
        Assertions.assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.getOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.getPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenre, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertTrue(actualVideo.getVideo().isEmpty());
        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());
        Assertions.assertEquals(actualVideo.getBanner().get(), aImageVideo);
        Assertions.assertTrue(actualVideo.getThumbnail().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));

    }

    @Test
    public void givenValidParameters_whenCallSetThumbnail_thenInstantiateVideo() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        final var aImageVideo = ImageMedia.with("abc", "Banner.jpg", "/132/video");
        // when
        final var actualVideo = Video.with(aVideo).updateThumbnailMedia(aImageVideo);

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());
        Assertions.assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertNotNull(actualVideo.getUpdatedAt());
        Assertions.assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
        Assertions.assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.getOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.getPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenre, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertTrue(actualVideo.getVideo().isEmpty());
        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());
        Assertions.assertTrue(actualVideo.getBanner().isEmpty());
        Assertions.assertEquals(actualVideo.getThumbnail().get(), aImageVideo);
        Assertions.assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));

    }

    @Test
    public void givenValidParameters_whenCallSetThumbnailHalf_thenInstantiateVideo() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        final var aImageVideo = ImageMedia.with("abc", "Banner.jpg", "/132/video");
        // when
        final var actualVideo = Video.with(aVideo).updateThumbnailHalfMedia(aImageVideo);

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());
        Assertions.assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertNotNull(actualVideo.getUpdatedAt());
        Assertions.assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
        Assertions.assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.getOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.getPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenre, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertTrue(actualVideo.getVideo().isEmpty());
        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());
        Assertions.assertTrue(actualVideo.getBanner().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnail().isEmpty());
        Assertions.assertEquals(actualVideo.getThumbnailHalf().get(), aImageVideo);

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));

    }

    @Test
    public void givenValidVideo_whenCallWith_shouldCreateWithoutParameters() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        // when
        final var actualVideo = Video.with(
            VideoID.unique().getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            InstantUtils.now(),
            InstantUtils.now(),
            null,
            null,
            null,
            null,
            null,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );

        // then
        Assertions.assertNotNull(actualVideo.getDomainEvents().isEmpty());

    }
}
