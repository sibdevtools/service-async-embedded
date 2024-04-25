package com.github.simple_mocks.async.local.service;

import com.github.simple_mocks.async.local.conf.LocalAsyncCleanUpServiceProperties;
import com.github.simple_mocks.async.local.conf.LocalAsyncServiceProperties;
import com.github.simple_mocks.async.local.entity.AsyncTaskStatus;
import com.github.simple_mocks.async.local.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.local.repository.AsyncTaskParamEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Slf4j
public class LocalAsyncCleanUpService {
    private final LocalAsyncCleanUpServiceProperties properties;
    private final AsyncTaskParamEntityRepository asyncTaskParamEntityRepository;
    private final AsyncTaskEntityRepository asyncTaskEntityRepository;

    public LocalAsyncCleanUpService(LocalAsyncServiceProperties properties,
                                    AsyncTaskParamEntityRepository asyncTaskParamEntityRepository,
                                    AsyncTaskEntityRepository asyncTaskEntityRepository) {
        this.properties = properties.getCleanUp();
        this.asyncTaskParamEntityRepository = asyncTaskParamEntityRepository;
        this.asyncTaskEntityRepository = asyncTaskEntityRepository;
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRES_NEW
    )
    @Scheduled(cron = "${service.local.async.clean-up.cron}", scheduler = "localAsyncScheduledExecutor")
    public void execute() {
        var finalStatuses = Arrays.stream(AsyncTaskStatus.values())
                .filter(AsyncTaskStatus::isFinalStatus)
                .toList();
        var maxRemovedAtOnce = properties.getMaxRemovedAtOnce();
        var pageable = Pageable.ofSize(maxRemovedAtOnce);

        var taskTTLType = properties.getTaskTTLType();
        var taskTTL = properties.getTaskTTL();
        var lastAttemptAt = ZonedDateTime.now()
                .minus(Duration.of(taskTTL, taskTTLType));

        var toDelete = asyncTaskEntityRepository.findUidsToDelete(
                lastAttemptAt,
                finalStatuses,
                pageable
        );

        log.info("Delete params");
        asyncTaskParamEntityRepository.deleteAllByEntityId_UidIn(toDelete);
        log.info("Delete tasks");
        asyncTaskEntityRepository.deleteAllById(toDelete);
        log.info("Commit");
    }

}
