package com.fullcycle.admin.catalogo.infrastructure.configuration;

import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.google.GoogleStorageProperties;
import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.storage.StorageProperties;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;
import com.fullcycle.admin.catalogo.infrastructure.services.impl.GPStorageService;
import com.fullcycle.admin.catalogo.infrastructure.services.local.InMemoryLocalStorageService;
import com.google.cloud.storage.Storage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StorageConfig {

    @Bean
    @ConfigurationProperties("storage.catalogo-videos")
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }

    @Bean
    @Profile({"development", "production"})
    public StorageService storageService(
        final GoogleStorageProperties properties,
        final Storage storage
        ) {
        return new GPStorageService(properties.getBucket(), storage);
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageService inMemoryStorageService() {
        return new InMemoryLocalStorageService();
    }
}
