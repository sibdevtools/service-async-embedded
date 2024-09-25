package com.github.sibdevtools.async.embedded.service;

import com.github.sibdevtools.async.embedded.configuration.properties.AsyncCleanUpServiceProperties;
import com.github.sibdevtools.async.embedded.configuration.properties.AsyncServiceEmbeddedProperties;
import com.github.sibdevtools.async.embedded.entity.AsyncTaskStatus;
import com.github.sibdevtools.async.embedded.repository.AsyncTaskEntityRepository;
import com.github.sibdevtools.async.embedded.repository.AsyncTaskParamEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
@Service
@ConditionalOnProperty(name = "service.async.mode", havingValue = "EMBEDDED")
public class AsyncCleanUpServiceEmbedded {
    private final AsyncCleanUpServiceProperties properties;
    private final AsyncTaskParamEntityRepository asyncTaskParamEntityRepository;
    private final AsyncTaskEntityRepository asyncTaskEntityRepository;

    @Autowired
    public AsyncCleanUpServiceEmbedded(AsyncServiceEmbeddedProperties properties,
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
    @Scheduled(cron = "${service.async.embedded.clean-up.cron}", scheduler = "asyncScheduledExecutor")
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

        asyncTaskParamEntityRepository.deleteAllByEntityId_UidIn(toDelete);

        asyncTaskEntityRepository.deleteAllById(toDelete);
    }

}
