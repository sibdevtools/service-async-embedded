package com.github.sibdevtools.async.embedded.service;

import com.github.sibdevtools.async.api.rq.CreateAsyncTaskRq;
import com.github.sibdevtools.async.api.rs.CreateAsyncTaskRs;
import com.github.sibdevtools.async.api.service.AsyncTaskService;
import com.github.sibdevtools.async.embedded.entity.AsyncTaskEntity;
import com.github.sibdevtools.async.embedded.entity.AsyncTaskParamEntity;
import com.github.sibdevtools.async.embedded.entity.AsyncTaskParamEntityId;
import com.github.sibdevtools.async.embedded.entity.AsyncTaskStatus;
import com.github.sibdevtools.async.embedded.repository.AsyncTaskEntityRepository;
import com.github.sibdevtools.async.embedded.repository.AsyncTaskParamEntityRepository;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Service
@ConditionalOnProperty(name = "service.async.mode", havingValue = "EMBEDDED")
public class AsyncTaskServiceEmbedded implements AsyncTaskService {
    private final AsyncTaskEntityRepository asyncTaskEntityRepository;
    private final AsyncTaskParamEntityRepository asyncTaskParamEntityRepository;

    /**
     * Construct async task registration embedded service
     *
     * @param asyncTaskEntityRepository      entity repository
     * @param asyncTaskParamEntityRepository param entity repository
     */
    @Autowired
    public AsyncTaskServiceEmbedded(AsyncTaskEntityRepository asyncTaskEntityRepository,
                                    AsyncTaskParamEntityRepository asyncTaskParamEntityRepository) {
        this.asyncTaskEntityRepository = asyncTaskEntityRepository;
        this.asyncTaskParamEntityRepository = asyncTaskParamEntityRepository;
    }

    @Nonnull
    @Override
    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRES_NEW
    )
    public CreateAsyncTaskRs registerTask(@Nonnull CreateAsyncTaskRq rq) {

        var asyncTaskEntity = AsyncTaskEntity.builder()
                .uid(rq.uid())
                .type(rq.type())
                .version(rq.version())
                .status(AsyncTaskStatus.CREATED)
                .statusDescription("Task created")
                .retry(1)
                .createdAt(ZonedDateTime.now())
                .lastRetryAt(ZonedDateTime.now())
                .nextRetryAt(rq.scheduledStartTime())
                .build();

        asyncTaskEntity = asyncTaskEntityRepository.save(asyncTaskEntity);
        var taskUid = asyncTaskEntity.getUid();

        var parameters = rq.parameters().entrySet()
                .stream()
                .map(it -> AsyncTaskParamEntity.builder()
                        .entityId(
                                AsyncTaskParamEntityId.builder()
                                        .uid(taskUid)
                                        .name(it.getKey())
                                        .build()
                        )
                        .value(it.getValue())
                        .build()
                )
                .toList();

        asyncTaskParamEntityRepository.saveAll(parameters);

        return new CreateAsyncTaskRs(true);
    }
}
