package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.fullcycle.admin.catalogo.domain.video.VideoResource;
import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.storage.StorageProperties;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    private final StorageService storage;
    private final String filenamePattern;
    private final String locationPattern;

    public DefaultMediaResourceGateway(final StorageService storage, final StorageProperties prop) {
        this.storage = storage;
        this.filenamePattern = prop.getFilenamePattern();
        this.locationPattern = prop.getLocationPattern();
    }

    @Override
    public AudioVideoMedia storeAudioVideo(final VideoID anId, final VideoResource aVideoResource) {
        final var filepath = filepath(anId, aVideoResource.type());
        final var aResource = aVideoResource.resource();
        store(filepath, aResource);
        return AudioVideoMedia.with(aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public ImageMedia storeImage(final VideoID anId, final VideoResource aVideoResource) {
        final var filepath = filepath(anId, aVideoResource.type());
        final var aResource = aVideoResource.resource();
        store(filepath, aResource);
        return ImageMedia.with(aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public void clearResources(VideoID anId) {
        final var ids = this.storage.list(folder(anId));
        this.storage.deleteAll(ids);
    }

    @Override
    public Optional<Resource> getResource(VideoID anId, VideoMediaType aType) {
        return this.storage.get(filepath(anId, aType));
    }

    private String filename(final VideoMediaType type) {
        return filenamePattern.replace("{type}", type.name());
    }

    private String folder(final VideoID anId) {
        return locationPattern.replace("{videoId}", anId.getValue());
    }

    private String filepath(final VideoID anId, final VideoMediaType aType) {
        return folder(anId)
            .concat("/")
            .concat(filename(aType));
    }

    private void store(final String filepath, final Resource aResource) {
        this.storage.store(filepath, aResource);
    }
}
