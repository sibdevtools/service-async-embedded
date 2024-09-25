package com.github.sibdevtools.async.embedded.configuration;

import com.github.sibdevtools.async.embedded.configuration.properties.AsyncServiceEmbeddedFlywayProperties;
import com.github.sibdevtools.async.embedded.configuration.properties.AsyncServiceEmbeddedProperties;
import com.github.sibdevtools.async.embedded.service.AsyncTaskProcessorRegistryEmbedded;
import com.github.sibdevtools.error.mutable.api.source.ErrorLocalizationsJsonSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ErrorLocalizationsJsonSource(
        systemCode = "ASYNC_SERVICE",
        iso3Code = "eng",
        path = "classpath:/embedded/async/content/errors/eng.json"
)
@ErrorLocalizationsJsonSource(
        systemCode = "ASYNC_SERVICE",
        iso3Code = "rus",
        path = "classpath:/embedded/async/content/errors/rus.json"
)
@Configuration
@PropertySource("classpath:/embedded/async/application.properties")
@ConditionalOnProperty(name = "service.async.mode", havingValue = "EMBEDDED")
public class AsyncServiceEmbeddedConfig {

    @Bean
    public Flyway embeddedAsyncFlyway(AsyncServiceEmbeddedFlywayProperties configuration,
                                        DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .encoding(configuration.getEncoding())
                .locations(configuration.getLocations())
                .defaultSchema(configuration.getSchema())
                .schemas(configuration.getSchema())
                .placeholders(
                        Map.of(
                                "schema", configuration.getSchema()
                        )
                )
                .load();
    }

    @Bean
    public MigrateResult embeddedAsyncFlywayMigrateResult(
            @Qualifier("embeddedAsyncFlyway") Flyway flyway
    ) {
        return flyway.migrate();
    }

    @Bean
    public TaskScheduler asyncScheduledExecutor() {
        return new ThreadPoolTaskSchedulerBuilder()
                .threadNamePrefix("asyncExecutor-")
                .poolSize(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Bean
    public ExecutorService asyncTaskExecutor(AsyncServiceEmbeddedProperties properties) {
        var propertiesExecutor = properties.getExecutor();
        return Executors.newFixedThreadPool(propertiesExecutor.getParallelTasks());
    }

}
