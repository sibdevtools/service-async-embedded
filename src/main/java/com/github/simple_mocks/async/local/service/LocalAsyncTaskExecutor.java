package com.github.simple_mocks.async.local.service;

import com.github.simple_mocks.async.api.entity.AsyncTask;
import com.github.simple_mocks.async.api.rs.AsyncTaskProcessingResult;
import com.github.simple_mocks.async.api.rs.AsyncTaskProcessingResultBuilder;
import com.github.simple_mocks.async.api.rs.AsyncTaskProcessingRetryResult;
import com.github.simple_mocks.async.api.service.AsyncTaskProcessor;
import com.github.simple_mocks.async.local.conf.LocalAsyncExecutorServiceProperties;
import com.github.simple_mocks.async.local.conf.LocalAsyncServiceProperties;
import com.github.simple_mocks.async.local.entity.AsyncTaskEntity;
import com.github.simple_mocks.async.local.entity.AsyncTaskParamEntity;
import com.github.simple_mocks.async.local.entity.AsyncTaskStatus;
import com.github.simple_mocks.async.local.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.local.repository.AsyncTaskParamEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZonedDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Slf4j
public class LocalAsyncTaskExecutor {
    private final LocalAsyncTaskProcessorRegistry asyncTaskProcessorRegistry;
    private final AsyncTaskEntityRepository asyncTaskEntityRepository;
    private final AsyncTaskParamEntityRepository asyncTaskParamEntityRepository;
    private final LocalAsyncExecutorServiceProperties properties;
    private final ExecutorService localAsyncAsyncTaskExecutor;

    public LocalAsyncTaskExecutor(LocalAsyncTaskProcessorRegistry asyncTaskProcessorRegistry,
                                  AsyncTaskEntityRepository asyncTaskEntityRepository,
                                  AsyncTaskParamEntityRepository asyncTaskParamEntityRepository,
                                  LocalAsyncServiceProperties properties,
                                  ExecutorService localAsyncAsyncTaskExecutor) {
        this.asyncTaskProcessorRegistry = asyncTaskProcessorRegistry;
        this.asyncTaskEntityRepository = asyncTaskEntityRepository;
        this.asyncTaskParamEntityRepository = asyncTaskParamEntityRepository;
        this.properties = properties.getExecutor();
        this.localAsyncAsyncTaskExecutor = localAsyncAsyncTaskExecutor;
    }

    @Scheduled(fixedRateString = "${service.local.async.executor.rate}", scheduler = "localAsyncScheduledExecutor")
    public void execute() {
        var parallelTasks = properties.getParallelTasks();
        var pageable = Pageable.ofSize(parallelTasks);
        var tasks = asyncTaskEntityRepository.findAllByNextRetryAtBeforeOrderByNextRetryAt(
                ZonedDateTime.now(),
                pageable
        );
        var callables = tasks.stream()
                .map(this::buildCallable)
                .toList();

        try {
            var futures = localAsyncAsyncTaskExecutor.invokeAll(callables);
            for (var future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    log.error("Async task execution exception", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Async task interrupted exception", e);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Async task interrupted exception", e);
        }
    }

    private Callable<Void> buildCallable(AsyncTaskEntity asyncTaskEntity) {
        return () -> {
            var type = asyncTaskEntity.getType();
            var version = asyncTaskEntity.getVersion();

            AsyncTaskProcessor processor;
            try {
                processor = asyncTaskProcessorRegistry.getProcessor(type, version);
            } catch (Exception e) {
                log.error("Can't get processor for task type " + type + " and version " + version, e);
                asyncTaskEntity.setStatus(AsyncTaskStatus.FAILED);
                asyncTaskEntity.setLastRetryAt(ZonedDateTime.now());
                asyncTaskEntity.setStatusDescription("Task processor obtain error");
                asyncTaskEntityRepository.save(asyncTaskEntity);
                return null;
            }
            var parameters = asyncTaskParamEntityRepository.findAllByEntityId_Uid(asyncTaskEntity.getUid())
                    .stream()
                    .collect(Collectors.toMap(it -> it.getEntityId().getName(), AsyncTaskParamEntity::getValue));

            var asyncTask = AsyncTask.builder()
                    .uid(asyncTaskEntity.getUid())
                    .type(asyncTaskEntity.getType())
                    .version(asyncTaskEntity.getVersion())
                    .retry(asyncTaskEntity.getRetry())
                    .createdAt(asyncTaskEntity.getCreatedAt())
                    .lastRetryAt(asyncTaskEntity.getLastRetryAt())
                    .parameters(parameters)
                    .build();
            AsyncTaskProcessingResult processingResult;
            try {
                processingResult = processor.process(asyncTask);
            } catch (Exception e) {
                log.error("Async task processing exception. Retry later.", e);
                var defaultRetryStep = processor.getDefaultRetryStep();
                var nextAttempt = ZonedDateTime.now().plus(defaultRetryStep);
                processingResult = AsyncTaskProcessingResultBuilder.createRetryResult(nextAttempt);
            }

            if (processingResult.isFinished()) {
                asyncTaskEntity.setStatus(AsyncTaskStatus.COMPLETED);
                asyncTaskEntity.setLastRetryAt(ZonedDateTime.now());
                asyncTaskEntity.setStatusDescription("Task completed successfully");
            } else {
                var retryProcessingResult = (AsyncTaskProcessingRetryResult) processingResult;
                var nextAttemptAt = retryProcessingResult.getNextAttemptAt();
                asyncTaskEntity.setRetry(asyncTaskEntity.getRetry() + 1);
                asyncTaskEntity.setNextRetryAt(nextAttemptAt);
                asyncTaskEntity.setLastRetryAt(ZonedDateTime.now());
                asyncTaskEntity.setStatus(AsyncTaskStatus.RETRYING);
                asyncTaskEntity.setStatusDescription("Retry is required");
            }
            asyncTaskEntityRepository.save(asyncTaskEntity);

            return null;
        };
    }
}
