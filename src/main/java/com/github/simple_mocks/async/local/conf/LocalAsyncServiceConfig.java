package com.github.simple_mocks.async.local.conf;

import com.github.simple_mocks.async.local.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.local.repository.AsyncTaskParamEntityRepository;
import com.github.simple_mocks.async.local.service.*;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class LocalAsyncServiceConfig {

    @Bean
    public LocalAsyncTaskProcessorRegistry localAsyncTaskProcessorRegistry() {
        return new LocalAsyncTaskProcessorRegistry();
    }

    @Bean
    public LocalAsyncTaskExecutor localAsyncTaskExecutor(LocalAsyncTaskProcessorRegistry registry,
                                                         AsyncTaskEntityRepository repository,
                                                         AsyncTaskParamEntityRepository paramERepository,
                                                         LocalAsyncServiceProperties properties,
                                                         ExecutorService executorService) {
        return new LocalAsyncTaskExecutor(registry, repository, paramERepository, properties, executorService);
    }

    @Bean
    public LocalAsyncTaskService localAsyncTaskService(AsyncTaskEntityRepository asyncTaskEntityRepository,
                                                       AsyncTaskParamEntityRepository asyncTaskParamEntityRepository) {
        return new LocalAsyncTaskService(asyncTaskEntityRepository, asyncTaskParamEntityRepository);
    }

    @Bean
    public TaskScheduler localAsyncScheduledExecutor() {
        return new ThreadPoolTaskSchedulerBuilder()
                .threadNamePrefix("localAsyncExecutor-")
                .poolSize(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Bean
    public ExecutorService localAsyncAsyncTaskExecutor(LocalAsyncServiceProperties properties) {
        var propertiesExecutor = properties.getExecutor();
        return Executors.newFixedThreadPool(propertiesExecutor.getParallelTasks());
    }

    @Bean
    public LocalAsyncServiceContextListener localAsyncServiceContextListener(LocalAsyncTaskProcessorRegistry registry) {
        return new LocalAsyncServiceContextListener(registry);
    }

    @Bean
    public LocalAsyncCleanUpService localAsyncCleanUpService(
            LocalAsyncServiceProperties properties,
            AsyncTaskParamEntityRepository asyncTaskParamEntityRepository,
            AsyncTaskEntityRepository asyncTaskEntityRepository) {
        return new LocalAsyncCleanUpService(properties, asyncTaskParamEntityRepository, asyncTaskEntityRepository);
    }
}
