package com.fullcycle.admin.catalogo.infrastructure.services.impl;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static com.google.cloud.storage.Storage.BlobListOption.prefix;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GPStorageServiceTest {

    private GPStorageService target;

    private Storage storage;

    private String bucket = "fc3_test";

    @BeforeEach
    public void setUp() {
        this.storage = Mockito.mock(Storage.class);
        this.target = new GPStorageService(this.bucket, this.storage);
    }

    @Test
    public void givenAValidResource_whenCallsStorage_shouldStorageIt() {
        //given
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var blob = mockBlob(expectedName, expectedResource);
        doReturn(blob).when(storage).create(any(Blob.class), any());

        //when
        this.target.store(expectedName, expectedResource);

        //then
        final var captor = ArgumentCaptor.forClass(BlobInfo.class);

        verify(storage, times(1)).create(captor.capture(), eq(expectedResource.content()));

        final var actualBlob = captor.getValue();
        Assertions.assertEquals(this.bucket, actualBlob.getBlobId().getBucket());
        Assertions.assertEquals(expectedName, actualBlob.getBlobId().getName());
        Assertions.assertEquals(expectedName, actualBlob.getName());
        Assertions.assertEquals(expectedResource.checksum(), actualBlob.getCrc32cToHexString());
        Assertions.assertEquals(expectedResource.contentType(), actualBlob.getContentType());
    }

    @Test
    public void givenAValidResource_whenCallsGet_shouldRetrievedIt() {
        //given
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var blob = mockBlob(expectedName, expectedResource);
        doReturn(blob).when(storage).get(anyString(), anyString());

        //when
        final var actualResource = this.target.get(expectedName).get();

        //then

        verify(storage, times(1)).get(eq(this.bucket), eq(expectedName));

        Assertions.assertEquals(expectedResource, actualResource);
    }

    @Test
    public void givenInvalidResource_whenCallsGet_shouldBeEmpty() {
        //given
        final var expectedName = IdUtils.uuid();

        doReturn(null).when(storage).get(anyString(), anyString());

        //when
        final var actualResource = this.target.get(expectedName);

        //then

        verify(storage, times(1)).get(eq(this.bucket), eq(expectedName));

        Assertions.assertTrue(actualResource.isEmpty());
    }

    @Test
    public void givenAValidPrefix_whenCallsList_shouldRetrieveAll() {
        //given
        final var expectedPrefix = "media_";
        final var expectedNameVideo = expectedPrefix + IdUtils.uuid();
        final var expectedResourceVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final var expectedNameBanner = expectedPrefix + IdUtils.uuid();
        final var expectedResourceBanner = Fixture.Videos.resource(VideoMediaType.BANNER);

        final var expectedResources = List.of(expectedNameVideo, expectedNameBanner);
        final var blobVideo = mockBlob(expectedNameVideo, expectedResourceVideo);
        final var blobBanner = mockBlob(expectedNameBanner, expectedResourceBanner);
        final var page = Mockito.mock(Page.class);
        doReturn(List.of(blobVideo, blobBanner)).when(page).iterateAll();

        doReturn(page).when(storage).list(anyString(), any());

        //when
        final var actualResource = this.target.list(expectedPrefix);

        //then

        verify(storage, times(1)).list(eq(this.bucket), eq(prefix(expectedPrefix)));

        Assertions.assertTrue(expectedResources.size() == actualResource.size() &&
                              actualResource.containsAll(expectedResources));
    }

    @Test
    public void givenAValidNames_whenCallsDelete_shouldDeleteAll() {
        //given
        final var expectedPrefix = "media_";
        final var expectedNameVideo = expectedPrefix + IdUtils.uuid();
        final var expectedNameBanner = expectedPrefix + IdUtils.uuid();

        final var expectedResources = List.of(expectedNameVideo, expectedNameBanner);

        //when
        this.target.deleteAll(expectedResources);

        //then
        final var captor = ArgumentCaptor.forClass(List.class);

        verify(storage, times(1)).delete(captor.capture());

        final var actualResources = ((List<BlobId>) captor.getValue()).stream()
            .map(BlobId::getName)
            .toList();

        Assertions.assertTrue(expectedResources.size() == actualResources.size() &&
                              actualResources.containsAll(expectedResources));
    }

    private Blob mockBlob(String name, Resource resource) {
        final var blob = Mockito.mock(Blob.class);
        when(blob.getBlobId()).thenReturn(BlobId.of(this.bucket, name));
        when(blob.getCrc32cToHexString()).thenReturn(resource.checksum());
        when(blob.getContent()).thenReturn(resource.content());
        when(blob.getContentType()).thenReturn(resource.contentType());
        when(blob.getName()).thenReturn(resource.name());

        return blob;
    }

}