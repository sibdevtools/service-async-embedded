package com.github.simple_mocks.async.local.conf;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class LocalAsyncStarter {
    private final LocalAsyncServiceProperties properties;
    private final Flyway localAsyncFlyway;

    public LocalAsyncStarter(LocalAsyncServiceProperties properties,
                             @Qualifier("localAsyncFlyway") Flyway localAsyncFlyway) {
        this.properties = properties;
        this.localAsyncFlyway = localAsyncFlyway;
    }

    @SneakyThrows
    @PostConstruct
    @Transactional
    public void setUp() {
        var folders2create = Optional.ofNullable(properties.getFolders2Create())
                .orElseGet(Collections::emptyList);

        for (var folder2create : folders2create) {
            Files.createDirectories(Path.of(folder2create));
        }

        localAsyncFlyway.migrate();
    }
}
