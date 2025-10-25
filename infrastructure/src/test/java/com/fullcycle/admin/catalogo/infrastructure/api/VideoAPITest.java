package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.MediaOutput;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaOutput;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.GetVideoByIdUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.VideoOutput;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.ListVideosUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.VideosListOutput;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoUseCase;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.fullcycle.admin.catalogo.domain.video.VideoPreview;
import com.fullcycle.admin.catalogo.domain.video.VideoSearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = VideoAPI.class)
public class VideoAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateVideoUseCase createVideoUseCase;

    @MockBean
    private GetVideoByIdUseCase getVideoByIdUseCase;

    @MockBean
    private UpdateVideoUseCase updateVideoUseCase;

    @MockBean
    private DeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    private ListVideosUseCase listVideosUseCase;

    @MockBean
    private GetMediaUseCase getMediaUseCase;

    @MockBean
    private UploadMediaUseCase uploadMediaUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateVideo_shouldReturnVideoId() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var castMember = Fixture.CastMembers.castMember();
        final var genre = Fixture.Genres.genre();
        final var category = Fixture.Categories.category();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(category.getId().getValue());
        final var expectedCastMembersIds = Set.of(castMember.getId().getValue());
        final var expectedGenresIds = Set.of(genre.getId().getValue());

        final var expectedVideo = new MockMultipartFile("video_file", "video.mp4", "video/mp4", "VIDEO".getBytes());
        final var expectedTrailer =
            new MockMultipartFile("trailer_file", "video.mp4", "video/mp4", "TRAILER".getBytes());
        final var expectedBanner = new MockMultipartFile("banner_file", "banner.png", "image/png", "BANNER".getBytes());
        final var expectedThumb = new MockMultipartFile("thumb_file", "thumb.png", "image/png", "THUMB".getBytes());
        final var expectedThumbHalf =
            new MockMultipartFile("thumb_half_file", "thumb_half.png", "image/png", "THUMB_HALF".getBytes());

        when(createVideoUseCase.execute(any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        // when
        final var aRequest = multipart("/videos").file(expectedVideo)
            .file(expectedTrailer)
            .file(expectedBanner)
            .file(expectedThumb)
            .file(expectedThumbHalf)
            .param("title", expectedTitle)
            .param("description", expectedDescription)
            .param("year_launched", String.valueOf(expectedLaunchAt.getValue()))
            .param("duration", String.valueOf(expectedDuration))
            .param("opened", String.valueOf(expectedOpened))
            .param("published", String.valueOf(expectedPublished))
            .param("rating", expectedRating.getName())
            .param("cast_member_id", castMember.getId().getValue())
            .param("categories_id", category.getId().getValue())
            .param("genres_id", genre.getId().getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(aRequest)
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(cmdCaptor.capture());

        CreateVideoCommand actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualCmd.title());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedLaunchAt.getValue(), actualCmd.launchAt());
        Assertions.assertEquals(expectedDuration, actualCmd.duration());
        Assertions.assertEquals(expectedRating.getName(), actualCmd.rating());
        Assertions.assertEquals(expectedOpened, actualCmd.opened());
        Assertions.assertEquals(expectedPublished, actualCmd.published());
        Assertions.assertEquals(expectedCategoriesIds, actualCmd.categories());
        Assertions.assertEquals(expectedCastMembersIds, actualCmd.members());
        Assertions.assertEquals(expectedGenresIds, actualCmd.genres());
        Assertions.assertEquals(expectedVideo.getOriginalFilename(), actualCmd.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.getOriginalFilename(), actualCmd.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.getOriginalFilename(), actualCmd.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.getOriginalFilename(), actualCmd.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.getOriginalFilename(), actualCmd.getThumbnailHalf().get().name());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateVideo_shouldReturnError() throws Exception {
        // given
        final var expectedErrorMessage = "title is required";

        when(createVideoUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = multipart("/videos");

        final var response = this.mvc.perform(aRequest);

        // then
        response
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

    }

    @Test
    public void givenAValidCommand_whenCallsCreatePartial_shouldReturnId() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var castMember = Fixture.CastMembers.castMember();
        final var genre = Fixture.Genres.genre();
        final var category = Fixture.Categories.category();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(category.getId().getValue());
        final var expectedCastMembersIds = Set.of(castMember.getId().getValue());
        final var expectedGenresIds = Set.of(genre.getId().getValue());

        final var aCmd = new CreateVideoRequest(
            expectedTitle,
            expectedDescription,
            expectedDuration,
            expectedLaunchAt.getValue(),
            expectedOpened,
            expectedPublished,
            expectedRating.getName(),
            expectedCastMembersIds,
            expectedCategoriesIds,
            expectedGenresIds);

        when(createVideoUseCase.execute(any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        // when
        final var aRequest = post("/videos").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(aCmd));

        this.mvc.perform(aRequest)
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(cmdCaptor.capture());

        CreateVideoCommand actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualCmd.title());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedLaunchAt.getValue(), actualCmd.launchAt());
        Assertions.assertEquals(expectedDuration, actualCmd.duration());
        Assertions.assertEquals(expectedRating.getName(), actualCmd.rating());
        Assertions.assertEquals(expectedOpened, actualCmd.opened());
        Assertions.assertEquals(expectedPublished, actualCmd.published());
        Assertions.assertEquals(expectedCategoriesIds, actualCmd.categories());
        Assertions.assertEquals(expectedCastMembersIds, actualCmd.members());
        Assertions.assertEquals(expectedGenresIds, actualCmd.genres());
        Assertions.assertTrue(actualCmd.getVideo().isEmpty());
        Assertions.assertTrue(actualCmd.getTrailer().isEmpty());
        Assertions.assertTrue(actualCmd.getBanner().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnail().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnailHalf().isEmpty());
    }

    @Test
    public void givenAInvalidCommand_whenCallsCreatePartial_shouldError() throws Exception {
        // given
        final var expectedMessageError = "title is required";

        when(createVideoUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedMessageError)));

        // when
        final var aRequest = post("/videos")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "title": "Ola"
                }
                """);

        // then
        final var response = this.mvc.perform(aRequest);

        // then
        response
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedMessageError)));
    }

    @Test
    public void givenAEmptyBody_whenCallsCreatePartial_shouldBadRequest() throws Exception {
        // given
        final var aRequest = post("/videos")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);
        // when
        final var response = this.mvc.perform(aRequest);

        // then
        response
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAValidId_whenCallGetById_shouldReturnVideo() throws Exception {
        // given
        final var castMember = Fixture.CastMembers.castMember();
        final var genre = Fixture.Genres.genre();
        final var category = Fixture.Categories.category();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(category.getId().getValue());
        final var expectedCastMembersIds = Set.of(castMember.getId().getValue());
        final var expectedGenresIds = Set.of(genre.getId().getValue());

        final var expectedVideo = Fixture.Videos.audioVideo(VideoMediaType.VIDEO);
        final var expectedTrailer = Fixture.Videos.audioVideo(VideoMediaType.TRAILER);
        final var expectedBanner = Fixture.Videos.imageMedia(VideoMediaType.BANNER);
        final var expectedThumb = Fixture.Videos.imageMedia(VideoMediaType.THUMBNAIL);
        final var expectedThumbHalf = Fixture.Videos.imageMedia(VideoMediaType.THUMBNAIL_HALF);

        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                mapTo(expectedCategoriesIds, CategoryID::from),
                mapTo(expectedGenresIds, GenreID::from),
                mapTo(expectedCastMembersIds, CastMemberID::from))
            .updateVideoMedia(expectedVideo)
            .updateTrailerMedia(expectedTrailer)
            .updateBannerMedia(expectedBanner)
            .updateThumbnailMedia(expectedThumb)
            .updateThumbnailHalfMedia(expectedThumbHalf);

        final var expectedId = aVideo.getId();

        when(getVideoByIdUseCase.execute(expectedId.getValue())).thenReturn(VideoOutput.from(aVideo));

        // when
        final var aRequest = get("/videos/{id}", expectedId.getValue()).accept(MediaType.APPLICATION_JSON);

        final var aResult = this.mvc.perform(aRequest);

        // then
        aResult.andExpect(status().isOk());
        aResult.andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        aResult.andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));
        aResult.andExpect(jsonPath("$.title", equalTo(expectedTitle)));
        aResult.andExpect(jsonPath("$.description", equalTo(expectedDescription)));
        aResult.andExpect(jsonPath("$.year_launched", equalTo(expectedLaunchAt.getValue())));
        aResult.andExpect(jsonPath("$.duration", equalTo(expectedDuration)));
        aResult.andExpect(jsonPath("$.opened", equalTo(expectedOpened)));
        aResult.andExpect(jsonPath("$.published", equalTo(expectedPublished)));
        aResult.andExpect(jsonPath("$.rating", equalTo(expectedRating.getName())));
        aResult.andExpect(jsonPath("$.created_at", equalTo(aVideo.getCreatedAt().toString())));
        aResult.andExpect(jsonPath("$.updated_at", equalTo(aVideo.getUpdatedAt().toString())));
        aResult.andExpect(jsonPath("$.banner.id", equalTo(expectedBanner.id())));
        aResult.andExpect(jsonPath("$.banner.name", equalTo(expectedBanner.name())));
        aResult.andExpect(jsonPath("$.banner.location", equalTo(expectedBanner.location())));
        aResult.andExpect(jsonPath("$.banner.checksum", equalTo(expectedBanner.checksum())));
        aResult.andExpect(jsonPath("$.thumbnail.id", equalTo(expectedThumb.id())));
        aResult.andExpect(jsonPath("$.thumbnail.name", equalTo(expectedThumb.name())));
        aResult.andExpect(jsonPath("$.thumbnail.location", equalTo(expectedThumb.location())));
        aResult.andExpect(jsonPath("$.thumbnail.checksum", equalTo(expectedThumb.checksum())));
        aResult.andExpect(jsonPath("$.thumbnail_half.id", equalTo(expectedThumbHalf.id())));
        aResult.andExpect(jsonPath("$.thumbnail_half.name", equalTo(expectedThumbHalf.name())));
        aResult.andExpect(jsonPath("$.thumbnail_half.location", equalTo(expectedThumbHalf.location())));
        aResult.andExpect(jsonPath("$.thumbnail_half.checksum", equalTo(expectedThumbHalf.checksum())));
        aResult.andExpect(jsonPath("$.video.name", equalTo(expectedVideo.name())));
        aResult.andExpect(jsonPath("$.video.checksum", equalTo(expectedVideo.checksum())));
        aResult.andExpect(jsonPath("$.video.location", equalTo(expectedVideo.rawLocation())));
        aResult.andExpect(jsonPath("$.video.encoded_location", equalTo(expectedVideo.encodedLocation())));
        aResult.andExpect(jsonPath("$.video.status", equalTo(expectedVideo.status().name())));
        aResult.andExpect(jsonPath("$.trailer.name", equalTo(expectedTrailer.name())));
        aResult.andExpect(jsonPath("$.trailer.checksum", equalTo(expectedTrailer.checksum())));
        aResult.andExpect(jsonPath("$.trailer.location", equalTo(expectedTrailer.rawLocation())));
        aResult.andExpect(jsonPath("$.trailer.encoded_location", equalTo(expectedTrailer.encodedLocation())));
        aResult.andExpect(jsonPath("$.trailer.status", equalTo(expectedTrailer.status().name())));
        aResult.andExpect(jsonPath("$.categories_id", equalTo(new ArrayList(expectedCategoriesIds))));
        aResult.andExpect(jsonPath("$.genres_id", equalTo(new ArrayList(expectedGenresIds))));
        aResult.andExpect(jsonPath("$.cast_member_id", equalTo(new ArrayList(expectedCastMembersIds))));

    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideo_shouldReturnVideoId() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var castMember = Fixture.CastMembers.castMember();
        final var genre = Fixture.Genres.genre();
        final var category = Fixture.Categories.category();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(category.getId().getValue());
        final var expectedCastMembersIds = Set.of(castMember.getId().getValue());
        final var expectedGenresIds = Set.of(genre.getId().getValue());

        final var aCmd = new UpdateVideoRequest(
            expectedTitle,
            expectedDescription,
            expectedDuration,
            expectedLaunchAt.getValue(),
            expectedOpened,
            expectedPublished,
            expectedRating.getName(),
            expectedCastMembersIds,
            expectedCategoriesIds,
            expectedGenresIds);

        when(updateVideoUseCase.execute(any())).thenReturn(new UpdateVideoOutput(expectedId.getValue()));

        // when
        final var aRequest = put("/videos/{id}", expectedId.getValue()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(aCmd));

        this.mvc.perform(aRequest)
            .andExpect(status().isOk())
            .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var cmdCaptor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

        verify(updateVideoUseCase).execute(cmdCaptor.capture());

        UpdateVideoCommand actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualCmd.title());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedLaunchAt.getValue(), actualCmd.launchAt());
        Assertions.assertEquals(expectedDuration, actualCmd.duration());
        Assertions.assertEquals(expectedRating.getName(), actualCmd.rating());
        Assertions.assertEquals(expectedOpened, actualCmd.opened());
        Assertions.assertEquals(expectedPublished, actualCmd.published());
        Assertions.assertEquals(expectedCategoriesIds, actualCmd.categories());
        Assertions.assertEquals(expectedCastMembersIds, actualCmd.members());
        Assertions.assertEquals(expectedGenresIds, actualCmd.genres());
        Assertions.assertTrue(actualCmd.getVideo().isEmpty());
        Assertions.assertTrue(actualCmd.getTrailer().isEmpty());
        Assertions.assertTrue(actualCmd.getBanner().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnail().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnailHalf().isEmpty());
    }

    @Test
    public void givenAnInvalidCommand_whenCallUpdate_shouldNotification() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var castMember = Fixture.CastMembers.castMember();
        final var genre = Fixture.Genres.genre();
        final var category = Fixture.Categories.category();
        final var expectedTitle = "";
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchAt = Year.of(Fixture.releaseYear());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategoriesIds = Set.of(category.getId().getValue());
        final var expectedCastMembersIds = Set.of(castMember.getId().getValue());
        final var expectedGenresIds = Set.of(genre.getId().getValue());

        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;

        final var aCmd = new UpdateVideoRequest(
            expectedTitle,
            expectedDescription,
            expectedDuration,
            expectedLaunchAt.getValue(),
            expectedOpened,
            expectedPublished,
            expectedRating.getName(),
            expectedCastMembersIds,
            expectedCategoriesIds,
            expectedGenresIds);

        when(updateVideoUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = put("/videos/{id}", expectedId.getValue()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(aCmd));

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
            .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateVideoUseCase).execute(any());

    }

    @Test
    public void givenAValidId_whenCallDeleteById_shouldDeletedIt() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        doNothing().when(deleteVideoUseCase).execute(expectedId.getValue());

        // when
        final var aRequest = delete("/videos/{id}", expectedId.getValue());
        final var response = mvc.perform(aRequest);

        // then
        response.andExpect(status().isNoContent());
        verify(deleteVideoUseCase).execute(any());
    }

    @Test
    public void givenValidParam_whenCallsListVideos_shouldReturnPagination() throws Exception {
        // given
        final var aVideo = VideoPreview.from(Fixture.Videos.systemDesigner());

        final var expectedPage = 50;
        final var expectedPerPage = 50;
        final var expectedTerms = "system";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "member1";
        final var expectedCategories = "category1";
        final var expectedGenres = "genre1";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideosListOutput.from(aVideo));

        when(listVideosUseCase.execute(any()))
            .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/videos")
            .queryParam("page", String.valueOf(expectedPage))
            .queryParam("perPage", String.valueOf(expectedPerPage))
            .queryParam("sort", expectedSort)
            .queryParam("dir", expectedDirection)
            .queryParam("search", expectedTerms)
            .queryParam("cast_members_ids", expectedCastMembers)
            .queryParam("categories_ids", expectedCategories)
            .queryParam("genres_ids", expectedGenres)
            .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
            .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
            .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
            .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
            .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
            .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
            .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())))
            .andExpect(jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())))
            .andExpect(jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString())));

        final var aCaptor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideosUseCase).execute(aCaptor.capture());

        final var actualQuery = aCaptor.getValue();
        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(Set.of(CategoryID.from(expectedCategories)), actualQuery.categories());
        Assertions.assertEquals(Set.of(CastMemberID.from(expectedCastMembers)), actualQuery.castMembers());
        Assertions.assertEquals(Set.of(GenreID.from(expectedGenres)), actualQuery.genres());
    }

    @Test
    public void givenEmptyParam_whenCallsListVideosWithDefault_shouldReturnPagination() throws Exception {
        // given
        final var aVideo = VideoPreview.from(Fixture.Videos.systemDesigner());

        final var expectedPage = 0;
        final var expectedPerPage = 25;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "";
        final var expectedCategories = "";
        final var expectedGenres = "";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideosListOutput.from(aVideo));

        when(listVideosUseCase.execute(any()))
            .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/videos");

        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
            .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
            .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
            .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
            .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
            .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
            .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())))
            .andExpect(jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())))
            .andExpect(jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString())));

        final var aCaptor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideosUseCase).execute(aCaptor.capture());

        final var actualQuery = aCaptor.getValue();
        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertTrue(actualQuery.categories().isEmpty());
        Assertions.assertTrue(actualQuery.castMembers().isEmpty());
        Assertions.assertTrue(actualQuery.genres().isEmpty());
    }

    public void givenAValidVideoIdAndFiletype_whenCallGetMediaById_shouldReturnContent() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedMediaType);

        final var expectedMedia = new MediaOutput(expectedResource.content(), expectedResource.contentType(), expectedResource.name());

        when(getMediaUseCase.execute(any()))
            .thenReturn(expectedMedia);

        // when
        final var aRequest =
            get("/videos/{id}/media/{type}", expectedId.getValue(), expectedMediaType.name());

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMedia.contentType()))
            .andExpect(header().string(HttpHeaders.CONTENT_LENGTH, String.valueOf(expectedMedia.content().length)))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(expectedMedia.name())))
            .andExpect(content().bytes(expectedMedia.content()));

        final var aCaptor = ArgumentCaptor.forClass(GetMediaCommand.class);
        verify(getMediaUseCase).execute(aCaptor.capture());

        final var actualCmd = aCaptor.getValue();
        Assertions.assertEquals(expectedId.getValue(), actualCmd.videoId());
        Assertions.assertEquals(expectedMediaType.name(), actualCmd.mediaType());
    }

    @Test
    public void givenAValidVideoIdAndFile_whenCallsUploadMedia_shouldStoreIt() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);

        final var expectedVideo = new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        final var expectedMedia = new UploadMediaOutput(expectedId.getValue(), expectedType);
        when(this.uploadMediaUseCase.execute(any()))
            .thenReturn(expectedMedia);

        // when
        final var aRequest = multipart("/videos/{id}/medias/{type}", expectedId.getValue(), expectedType.name())
            .file(expectedVideo)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA);
        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/videos/%s/medias/%s".formatted(expectedId.getValue(), expectedType.name())))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.video_id", equalTo(expectedId.getValue())))
            .andExpect(jsonPath("$.media_type", equalTo(expectedType.name())));

        final var aCaptor = ArgumentCaptor.forClass(UploadMediaCommand.class);
        verify(this.uploadMediaUseCase).execute(aCaptor.capture());

        final var actualCmd = aCaptor.getValue();
        Assertions.assertEquals(expectedId.getValue(), actualCmd.videoId());
        Assertions.assertEquals(expectedType, actualCmd.videoResource().type());
        Assertions.assertEquals(expectedResource.name(), actualCmd.videoResource().resource().name());
        Assertions.assertEquals(expectedResource.contentType(), actualCmd.videoResource().resource().contentType());
        Assertions.assertEquals(expectedResource.content(), actualCmd.videoResource().resource().content());
    }

    @Test
    public void givenAnInvalidVideoType_whenCallsUploadMedia_shouldError() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final var expectedErrorMessage = "Media type INVALID is not supported";

        final var expectedVideo = new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        // when
        final var aRequest = multipart("/videos/{id}/medias/INVALID", expectedId.getValue())
            .file(expectedVideo)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA);
        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isUnprocessableEntity())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

    }
}