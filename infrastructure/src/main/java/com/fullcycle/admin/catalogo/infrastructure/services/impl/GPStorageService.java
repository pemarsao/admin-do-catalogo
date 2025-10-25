package com.fullcycle.admin.catalogo.infrastructure.services.impl;

import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class GPStorageService implements StorageService {

    private final String bucket;
    private final Storage storage;

    public GPStorageService(final String bucket, final Storage storage) {
        this.bucket = bucket;
        this.storage = storage;
    }

    @Override
    public void deleteAll(final Collection<String> names) {
        final var blobs = names.stream()
                .map(name -> BlobId.of(this.bucket, name))
                .toList();

        this.storage.delete(blobs);
    }

    @Override
    public Optional<Resource> get(String name) {
        return Optional.ofNullable(this.storage.get(this.bucket, name))
                .map(blob -> Resource.with(blob.getCrc32cToHexString(), blob.getContent(), blob.getContentType(),
                    blob.getName()));
    }

    @Override
    public List<String> list(String prefix) {
        final var blobs = this.storage.list(this.bucket, Storage.BlobListOption.prefix(prefix));
        return StreamSupport.stream(blobs.iterateAll().spliterator(), false)
                .map(BlobInfo::getBlobId)
                .map(BlobId::getName)
                .toList();
    }

    @Override
    public void store(String name, Resource resource) {
        final var blobInfo = BlobInfo.newBuilder(this.bucket, name)
                .setContentType(resource.contentType())
                .setCrc32cFromHexString(resource.checksum())
                .build();
        this.storage.create(blobInfo, resource.content());
    }
}
