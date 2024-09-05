package com.github.simple_mocks.async.embedded.configuration;

import com.github.simple_mocks.async.embedded.configuration.properties.AsyncServiceEmbeddedProperties;
import com.github.simple_mocks.async.embedded.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.embedded.repository.AsyncTaskParamEntityRepository;
import com.github.simple_mocks.async.embedded.service.*;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@PropertySource("classpath:embedded-async-application.properties")
public class AsyncServiceEmbeddedConfig {
    @Bean
    @ConfigurationProperties("spring.flyway.embedded-async")
    public ClassicConfiguration asyncFlywayConfiguration(DataSource dataSource) {
        var classicConfiguration = new ClassicConfiguration();
        classicConfiguration.setDataSource(dataSource);
        return classicConfiguration;
    }

    @Bean
    public Flyway asyncEmbeddedFlyway(@Qualifier("asyncFlywayConfiguration") ClassicConfiguration configuration) {
        var flyway = new Flyway(configuration);
        flyway.migrate();
        return flyway;
    }

    @Bean
    public AsyncTaskProcessorRegistryEmbedded asyncTaskProcessorRegistryEmbedded() {
        return new AsyncTaskProcessorRegistryEmbedded();
    }

    @Bean
    public AsyncTaskExecutorEmbedded asyncTaskExecutorEmbedded(
            AsyncTaskProcessorRegistryEmbedded registry,
            AsyncTaskEntityRepository repository,
            AsyncTaskParamEntityRepository paramERepository,
            AsyncServiceEmbeddedProperties properties,
            ExecutorService executorService
    ) {
        return new AsyncTaskExecutorEmbedded(registry, repository, paramERepository, properties, executorService);
    }

    @Bean
    public AsyncTaskServiceEmbedded asyncTaskServiceEmbedded(
            AsyncTaskEntityRepository asyncTaskEntityRepository,
            AsyncTaskParamEntityRepository asyncTaskParamEntityRepository
    ) {
        return new AsyncTaskServiceEmbedded(asyncTaskEntityRepository, asyncTaskParamEntityRepository);
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

    @Bean
    public AsyncServiceContextListener asyncServiceEmbeddedContextListener(
            AsyncTaskProcessorRegistryEmbedded registry
    ) {
        return new AsyncServiceContextListener(registry);
    }

    @Bean
    public AsyncCleanUpServiceEmbedded asyncCleanUpServiceEmbedded(
            AsyncServiceEmbeddedProperties properties,
            AsyncTaskParamEntityRepository asyncTaskParamEntityRepository,
            AsyncTaskEntityRepository asyncTaskEntityRepository) {
        return new AsyncCleanUpServiceEmbedded(properties, asyncTaskParamEntityRepository, asyncTaskEntityRepository);
    }
}
