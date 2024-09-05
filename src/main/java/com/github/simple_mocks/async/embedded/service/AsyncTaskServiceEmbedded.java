package com.github.simple_mocks.async.embedded.service;

import com.github.simple_mocks.async.api.rq.CreateAsyncTaskRq;
import com.github.simple_mocks.async.api.service.AsyncTaskService;
import com.github.simple_mocks.async.embedded.entity.AsyncTaskEntity;
import com.github.simple_mocks.async.embedded.entity.AsyncTaskParamEntity;
import com.github.simple_mocks.async.embedded.entity.AsyncTaskParamEntityId;
import com.github.simple_mocks.async.embedded.entity.AsyncTaskStatus;
import com.github.simple_mocks.async.embedded.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.embedded.repository.AsyncTaskParamEntityRepository;
import jakarta.annotation.Nonnull;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class AsyncTaskServiceEmbedded implements AsyncTaskService {
    private final AsyncTaskEntityRepository asyncTaskEntityRepository;
    private final AsyncTaskParamEntityRepository asyncTaskParamEntityRepository;

    /**
     * Construct async task registration embedded service
     *
     * @param asyncTaskEntityRepository      entity repository
     * @param asyncTaskParamEntityRepository param entity repository
     */
    public AsyncTaskServiceEmbedded(AsyncTaskEntityRepository asyncTaskEntityRepository,
                                    AsyncTaskParamEntityRepository asyncTaskParamEntityRepository) {
        this.asyncTaskEntityRepository = asyncTaskEntityRepository;
        this.asyncTaskParamEntityRepository = asyncTaskParamEntityRepository;
    }

    @Override
    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRES_NEW
    )
    public boolean registerTask(@Nonnull CreateAsyncTaskRq rq) {

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

        return true;
    }
}
