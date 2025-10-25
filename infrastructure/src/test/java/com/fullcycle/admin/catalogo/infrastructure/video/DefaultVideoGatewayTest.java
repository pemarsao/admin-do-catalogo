package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
public class DefaultVideoGatewayTest {

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private VideoRepository videoRepository;

    private Category anime;
    private Category filmes;
    private Genre aventura;
    private Genre ficcao;
    private CastMember vinDiesel;
    private CastMember jasonMomoa;
    private CastMember stevenSpielberg;

    @BeforeEach
    public void setUp() {
        anime = categoryGateway.create(Category.newCategory(Fixture.Categories.ANIME.getName(), "", true));
        filmes = categoryGateway.create(Category.newCategory(Fixture.Categories.FILMES.getName(), "", true));

        aventura = genreGateway.create(Genre.newGenre(Fixture.Genres.AVENTURA.getName(), true));
        ficcao = genreGateway.create(Genre.newGenre(Fixture.Genres.FICCAO.getName(), true));

        vinDiesel = castMemberGateway.create(CastMember.newMember(Fixture.CastMembers.VIN_DIESEL.getName(), CastMemberType.ACTOR));
        jasonMomoa = castMemberGateway.create(CastMember.newMember(Fixture.CastMembers.JASON_MOMOA.getName(), CastMemberType.ACTOR));
        stevenSpielberg = castMemberGateway.create(CastMember.newMember(Fixture.CastMembers.STEVEN_SPIELBERG.getName(), CastMemberType.DIRECTOR));
    }

    @Test
    public void instance() {
        assertNotNull(videoGateway);
        assertNotNull(categoryGateway);
        assertNotNull(genreGateway);
        assertNotNull(castMemberGateway);
        assertNotNull(videoRepository);
    }

    @Test
    @Transactional
    public void givenValidVideo_whenCallsCreateVideo_shouldPersistVideo() {
        // given

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(filmes.getId());
        final var expectedCastMembersIds = Set.of(vinDiesel.getId());
        final var expectedGenresIds = Set.of(aventura.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with(IdUtils.uuid(), "32323232", "video", "/videos/32323232");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with(IdUtils.uuid(), "32323232", "trailer", "/trailers/32323232");
        final ImageMedia expectedBanner = ImageMedia.with(IdUtils.uuid(), "32323232", "banner", "/banners/32323232");
        final ImageMedia expectedThumb = ImageMedia.with(IdUtils.uuid(), "32323232", "thumbnail", "/thumbnails/32323232");
        final ImageMedia expectedThumbHalf = ImageMedia.with(IdUtils.uuid(), "32323232", "thumbnail_half", "/thumbnails_half/32323232");

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
            )
            .updateVideoMedia(expectedVideo)
            .updateTrailerMedia(expectedTrailer)
            .updateBannerMedia(expectedBanner)
            .updateThumbnailMedia(expectedThumb)
            .updateThumbnailHalfMedia(expectedThumbHalf);

        // when
        final var actualOutput = videoGateway.create(aVideo);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.getId());


        Assertions.assertEquals(expectedTitle, aVideo.getTitle());
        Assertions.assertEquals(expectedDescription, aVideo.getDescription());
        Assertions.assertEquals(expectedLaunchAt, aVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, aVideo.getDuration());
        Assertions.assertEquals(expectedRating, aVideo.getRating());
        Assertions.assertEquals(expectedOpened, aVideo.getOpened());
        Assertions.assertEquals(expectedPublished, aVideo.getPublished());
        Assertions.assertEquals(expectedCategoriesIds, aVideo.getCategories());
        Assertions.assertEquals(expectedCastMembersIds, aVideo.getCastMembers());
        Assertions.assertEquals(expectedGenresIds, aVideo.getGenres());
        Assertions.assertEquals(expectedVideo.name(), aVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), aVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), aVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), aVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), aVideo.getThumbnailHalf().get().name());

        final var persistedVideo = videoRepository.findById(actualOutput.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchAt, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedCategoriesIds, persistedVideo.getCategoriesID());
        Assertions.assertEquals(expectedCastMembersIds, persistedVideo.getCastMembersID());
        Assertions.assertEquals(expectedGenresIds, persistedVideo.getGenresID());
        Assertions.assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        Assertions.assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        Assertions.assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        Assertions.assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        Assertions.assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());

    }

    @Test
    @Transactional
    public void givenValidVideoWithoutRelations_whenCallsCreateVideo_shouldPersistVideo() {
        // given
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.<CategoryID>of();
        final var expectedCastMembersIds = Set.<CastMemberID>of();
        final var expectedGenresIds = Set.<GenreID>of();

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
            );

        // when
        final var actualOutput = videoGateway.create(aVideo);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.getId());


        Assertions.assertEquals(expectedTitle, aVideo.getTitle());
        Assertions.assertEquals(expectedDescription, aVideo.getDescription());
        Assertions.assertEquals(expectedLaunchAt, aVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, aVideo.getDuration());
        Assertions.assertEquals(expectedRating, aVideo.getRating());
        Assertions.assertEquals(expectedOpened, aVideo.getOpened());
        Assertions.assertEquals(expectedPublished, aVideo.getPublished());
        Assertions.assertEquals(expectedCategoriesIds, aVideo.getCategories());
        Assertions.assertEquals(expectedCastMembersIds, aVideo.getCastMembers());
        Assertions.assertEquals(expectedGenresIds, aVideo.getGenres());

        final var persistedVideo = videoRepository.findById(actualOutput.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchAt, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertTrue(persistedVideo.getCategories().isEmpty());
        Assertions.assertTrue(persistedVideo.getCastMembers().isEmpty());
        Assertions.assertTrue(persistedVideo.getGenres().isEmpty());
        Assertions.assertNull(persistedVideo.getVideo());
        Assertions.assertNull(persistedVideo.getTrailer());
        Assertions.assertNull(persistedVideo.getBanner());
        Assertions.assertNull(persistedVideo.getThumbnail());
        Assertions.assertNull(persistedVideo.getThumbnailHalf());

    }

    @Test
    @Transactional
    public void givenValidVideo_whenCallsUpdateVideo_shouldPersistVideo() {
        // given
        final var aVideo = videoGateway.create(
            Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.releaseYear()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.of(),
                Set.of(),
                Set.of()
            )
        );

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(filmes.getId());
        final var expectedCastMembersIds = Set.of(vinDiesel.getId());
        final var expectedGenresIds = Set.of(aventura.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with(IdUtils.uuid(), "32323232", "video", "/videos/32323232");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with(IdUtils.uuid(), "32323232", "trailer", "/trailers/32323232");
        final ImageMedia expectedBanner = ImageMedia.with(IdUtils.uuid(), "32323232", "banner", "/banners/32323232");
        final ImageMedia expectedThumb = ImageMedia.with(IdUtils.uuid(), "32323232", "thumbnail", "/thumbnails/32323232");
        final ImageMedia expectedThumbHalf = ImageMedia.with(IdUtils.uuid(), "32323232", "thumbnail_half", "/thumbnails_half/32323232");

        final var updatedVideo = aVideo.update(
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
            )
            .updateVideoMedia(expectedVideo)
            .updateTrailerMedia(expectedTrailer)
            .updateBannerMedia(expectedBanner)
            .updateThumbnailMedia(expectedThumb)
            .updateThumbnailHalfMedia(expectedThumbHalf);

        // when
        final var actualOutput = videoGateway.create(updatedVideo);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.getId());


        Assertions.assertEquals(expectedTitle, actualOutput.getTitle());
        Assertions.assertEquals(expectedDescription, actualOutput.getDescription());
        Assertions.assertEquals(expectedLaunchAt, actualOutput.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualOutput.getDuration());
        Assertions.assertEquals(expectedRating, actualOutput.getRating());
        Assertions.assertEquals(expectedOpened, actualOutput.getOpened());
        Assertions.assertEquals(expectedPublished, actualOutput.getPublished());
        Assertions.assertEquals(expectedCategoriesIds, actualOutput.getCategories());
        Assertions.assertEquals(expectedCastMembersIds, actualOutput.getCastMembers());
        Assertions.assertEquals(expectedGenresIds, actualOutput.getGenres());
        Assertions.assertEquals(expectedVideo.name(), actualOutput.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), actualOutput.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), actualOutput.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), actualOutput.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), actualOutput.getThumbnailHalf().get().name());
        Assertions.assertNotNull(actualOutput.getCreatedAt());
        Assertions.assertTrue(updatedVideo.getUpdatedAt().isAfter(updatedVideo.getCreatedAt()));

        final var persistedVideo = videoRepository.findById(actualOutput.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchAt, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedCategoriesIds, persistedVideo.getCategoriesID());
        Assertions.assertEquals(expectedCastMembersIds, persistedVideo.getCastMembersID());
        Assertions.assertEquals(expectedGenresIds, persistedVideo.getGenresID());
        Assertions.assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        Assertions.assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        Assertions.assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        Assertions.assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        Assertions.assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());
        Assertions.assertNotNull(persistedVideo.getCreatedAt());
        Assertions.assertTrue(persistedVideo.getUpdatedAt().isAfter(persistedVideo.getCreatedAt()));

    }

    @Test
    public void givenValidVideo_whenCallsDeleteVideoById_shouldDeletedIt() {
        // given
        final var aVideo = videoGateway.create(
            Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.releaseYear()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.of(),
                Set.of(),
                Set.of()
            )
        );
        Assertions.assertEquals(1, videoRepository.count());

        // when
        videoGateway.deleteById(aVideo.getId());

        // then
        Assertions.assertEquals(0, videoRepository.count());
    }

    @Test
    public void givenInvalidVideo_whenCallsDeleteVideoById_shouldDoNothing() {
        // given
        final var aVideo = videoGateway.create(
            Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.releaseYear()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.of(),
                Set.of(),
                Set.of()
            )
        );
        Assertions.assertEquals(1, videoRepository.count());

        // when
        videoGateway.deleteById(VideoID.from(IdUtils.uuid()));

        // then
        Assertions.assertEquals(1, videoRepository.count());
    }

    @Test
    public void givenValidVideo_whenCallsFindVideoById_shouldReturnIt() {
        // given

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(filmes.getId());
        final var expectedCastMembersIds = Set.of(vinDiesel.getId());
        final var expectedGenresIds = Set.of(aventura.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with(IdUtils.uuid(), "32323232", "video", "/videos/32323232");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with(IdUtils.uuid(), "32323232", "trailer", "/trailers/32323232");
        final ImageMedia expectedBanner = ImageMedia.with(IdUtils.uuid(), "32323232", "banner", "/banners/32323232");
        final ImageMedia expectedThumb = ImageMedia.with(IdUtils.uuid(), "32323232", "thumbnail", "/thumbnails/32323232");
        final ImageMedia expectedThumbHalf = ImageMedia.with(IdUtils.uuid(), "32323232", "thumbnail_half", "/thumbnails_half/32323232");

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
            )
            .updateVideoMedia(expectedVideo)
            .updateTrailerMedia(expectedTrailer)
            .updateBannerMedia(expectedBanner)
            .updateThumbnailMedia(expectedThumb)
            .updateThumbnailHalfMedia(expectedThumbHalf);

        videoGateway.create(aVideo);
        // when
        final var actualOutput = videoGateway.findById(aVideo.getId()).get();

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.getId());


        Assertions.assertEquals(expectedTitle, aVideo.getTitle());
        Assertions.assertEquals(expectedDescription, aVideo.getDescription());
        Assertions.assertEquals(expectedLaunchAt, aVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, aVideo.getDuration());
        Assertions.assertEquals(expectedRating, aVideo.getRating());
        Assertions.assertEquals(expectedOpened, aVideo.getOpened());
        Assertions.assertEquals(expectedPublished, aVideo.getPublished());
        Assertions.assertEquals(expectedCategoriesIds, aVideo.getCategories());
        Assertions.assertEquals(expectedCastMembersIds, aVideo.getCastMembers());
        Assertions.assertEquals(expectedGenresIds, aVideo.getGenres());
        Assertions.assertEquals(expectedVideo.name(), aVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), aVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), aVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), aVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), aVideo.getThumbnailHalf().get().name());

    }

    @Test
    public void givenInvalidVideoId_whenCallsFindVideoById_shouldReturnEmpty() {
        // given
        videoGateway.create(
            Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.releaseYear()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.of(),
                Set.of(),
                Set.of()
            )
        );
        // when
        final var actualOutput = videoGateway.findById(VideoID.unique());

        // then
        Assertions.assertFalse(actualOutput.isPresent());

    }

    @Test
    public void givenEmptyVideos_whenCallsFindAll_shouldReturnEmpty() {

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());

    }

    @Test
    public void givenVideos_whenCallsFindAll_shouldReturnAllList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 4;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());
    }

    @Test
    public void givenValidCategory_whenCallsFindAll_shouldReturnFilteredList() {

        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(filmes.getId()),
                Set.of());
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());

        Assertions.assertEquals("The Lord of the Rings: The Return of the King", actualResult.items().get(0).title());
        Assertions.assertEquals("The Lord of the Rings: The Two Towers", actualResult.items().get(1).title());

    }

    @Test
    public void givenValidCastMember_whenCallsFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(jasonMomoa.getId()),
                Set.of(),
                Set.of());
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());

        Assertions.assertEquals("Saint Seiya", actualResult.items().get(0).title());
        Assertions.assertEquals("The Lord of the Rings: The Return of the King", actualResult.items().get(1).title());
    }

    @Test
    public void givenValidGenre_whenCallsFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of(ficcao.getId()));
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());

        Assertions.assertEquals("The Lord of the Rings: The Two Towers", actualResult.items().get(0).title());
    }

    @Test
    public void givenAllParameters_whenCallsFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "The Lord of the Rings";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(stevenSpielberg.getId()),
                Set.of(filmes.getId()),
                Set.of(ficcao.getId()));
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());

        Assertions.assertEquals("The Lord of the Rings: The Two Towers", actualResult.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "title,asc,0,10,4,4,Saint Seiya",
            "title,desc,0,10,4,4,The Lord of the Rings: The Two Towers",
            "createdAt,asc,0,10,4,4,Saint Seiya",
            "createdAt,desc,0,10,4,4,The Lord of the Rings: The Return of the King",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnOrderer(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final long expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        mockVideos();
        final var expectedTerms = "";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).title());

    }

    @ParameterizedTest
    @CsvSource(value = {
            "saint,0,10,1,1,Saint Seiya",
            "towers,0,10,1,1,The Lord of the Rings: The Two Towers",
            "return,0,10,1,1,The Lord of the Rings: The Return of the King",
            "fellowship,0,10,1,1,The Lord of the Rings: The Fellowship of the Ring",
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final long expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {

        mockVideos();
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).title());

    }

    @ParameterizedTest
    @CsvSource(value = {
            "0,2,2,4,Saint Seiya;The Lord of the Rings: The Fellowship of the Ring",
            "1,2,2,4,The Lord of the Rings: The Return of the King;The Lord of the Rings: The Two Towers",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnPaged(
            final int expectedPage,
            final int expectedPerPage,
            final long expectedItemsCount,
            final long expectedTotal,
            final String expectedVideos
    ) {
        mockVideos();
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());
        final var actualResult = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());

        int index = 0;
        for (final var expectedTitle : expectedVideos.split(";")) {
            final var actualTitle = actualResult.items().get(index).title();
            Assertions.assertEquals(expectedTitle, actualTitle);
            index++;
        }
    }

    private void mockVideos() {

        videoGateway.create(
                Video.newVideo(
                        "Saint Seiya",
                        Fixture.Videos.description(),
                        Year.of(Fixture.releaseYear()),
                        Fixture.duration(),
                        Fixture.Videos.rating(),
                        Fixture.bool(),
                        Fixture.bool(),
                        Set.of(anime.getId()),
                        Set.of(aventura.getId()),
                        Set.of(vinDiesel.getId(), jasonMomoa.getId())
                )
        );

        videoGateway.create(
                Video.newVideo(
                        "The Lord of the Rings: The Fellowship of the Ring",
                        Fixture.Videos.description(),
                        Year.of(Fixture.releaseYear()),
                        Fixture.duration(),
                        Fixture.Videos.rating(),
                        Fixture.bool(),
                        Fixture.bool(),
                        Set.of(),
                        Set.of(),
                        Set.of()
                )
        );

        videoGateway.create(
                Video.newVideo(
                        "The Lord of the Rings: The Two Towers",
                        Fixture.Videos.description(),
                        Year.of(Fixture.releaseYear()),
                        Fixture.duration(),
                        Fixture.Videos.rating(),
                        Fixture.bool(),
                        Fixture.bool(),
                        Set.of(filmes.getId()),
                        Set.of(ficcao.getId()),
                        Set.of(stevenSpielberg.getId())
                )
        );

        videoGateway.create(
                Video.newVideo(
                        "The Lord of the Rings: The Return of the King",
                        Fixture.Videos.description(),
                        Year.of(Fixture.releaseYear()),
                        Fixture.duration(),
                        Fixture.Videos.rating(),
                        Fixture.bool(),
                        Fixture.bool(),
                        Set.of(filmes.getId()),
                        Set.of(aventura.getId()),
                        Set.of(jasonMomoa.getId())
                )
        );
    }


}