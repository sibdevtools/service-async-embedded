package com.github.simple_mocks.async.embedded;

import com.github.simple_mocks.async.api.rq.CreateAsyncTaskRq;
import com.github.simple_mocks.async.embedded.entity.AsyncTaskStatus;
import com.github.simple_mocks.async.embedded.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.embedded.service.AsyncCleanUpServiceEmbedded;
import com.github.simple_mocks.async.embedded.service.AsyncTaskExecutorEmbedded;
import com.github.simple_mocks.async.embedded.service.AsyncTaskServiceEmbedded;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ActiveProfiles("startup-test")
@EnableAsyncServiceEmbedded
@SpringBootApplication
class AsyncTaskExecutorEmbeddedIntegrationTest {
    @Test
    void testExecuteInvalidVersion() {
        try (var context = SpringApplication.run(AsyncTaskExecutorEmbeddedIntegrationTest.class)) {
            assertNotNull(context);

            var taskService = context.getBean(AsyncTaskServiceEmbedded.class);

            var taskUid = UUID.randomUUID().toString();
            var rq = CreateAsyncTaskRq.builder()
                    .uid(taskUid)
                    .type("test-type")
                    .version("v1")
                    .scheduledStartTime(ZonedDateTime.now().minusSeconds(30))
                    .parameters(
                            Map.of(
                                    "key", "value"
                            )
                    )
                    .build();
            assertTrue(taskService.registerTask(rq));

            var executor = context.getBean(AsyncTaskExecutorEmbedded.class);
            executor.execute();

            var asyncTaskEntityRepository = context.getBean(AsyncTaskEntityRepository.class);
            var asyncTaskEntity = asyncTaskEntityRepository.findById(taskUid)
                    .orElseThrow(() -> new IllegalArgumentException("Async task not found"));

            assertEquals(AsyncTaskStatus.FAILED, asyncTaskEntity.getStatus());

            asyncTaskEntity.setLastRetryAt(ZonedDateTime.now().minusMonths(1));

            asyncTaskEntityRepository.saveAndFlush(asyncTaskEntity);

            var cleanUpService = context.getBean(AsyncCleanUpServiceEmbedded.class);
            cleanUpService.execute();

            assertFalse(asyncTaskEntityRepository.existsById(taskUid));
        }
    }

    @Test
    void testExecute() {
        try (var context = SpringApplication.run(AsyncTaskExecutorEmbeddedIntegrationTest.class)) {
            assertNotNull(context);

            var taskService = context.getBean(AsyncTaskServiceEmbedded.class);

            var taskUid = UUID.randomUUID().toString();
            var rq = CreateAsyncTaskRq.builder()
                    .uid(taskUid)
                    .type("test-task-finish")
                    .version("v1")
                    .scheduledStartTime(ZonedDateTime.now().minusSeconds(30))
                    .parameters(
                            Map.of(
                                    "key", "value"
                            )
                    )
                    .build();
            assertTrue(taskService.registerTask(rq));

            var executor = context.getBean(AsyncTaskExecutorEmbedded.class);
            executor.execute();

            var asyncTaskEntityRepository = context.getBean(AsyncTaskEntityRepository.class);
            var asyncTaskEntity = asyncTaskEntityRepository.findById(taskUid)
                    .orElseThrow(() -> new IllegalArgumentException("Async task not found"));

            assertEquals(AsyncTaskStatus.COMPLETED, asyncTaskEntity.getStatus());

            asyncTaskEntity.setLastRetryAt(ZonedDateTime.now().minusMonths(1));

            asyncTaskEntityRepository.saveAndFlush(asyncTaskEntity);

            var cleanUpService = context.getBean(AsyncCleanUpServiceEmbedded.class);
            cleanUpService.execute();

            assertFalse(asyncTaskEntityRepository.existsById(taskUid));
        }
    }

    @Test
    void testExecuteRetry() {
        try (var context = SpringApplication.run(AsyncTaskExecutorEmbeddedIntegrationTest.class)) {
            assertNotNull(context);

            var taskService = context.getBean(AsyncTaskServiceEmbedded.class);

            var taskUid = UUID.randomUUID().toString();
            var rq = CreateAsyncTaskRq.builder()
                    .uid(taskUid)
                    .type("test-task-retry")
                    .version("v1")
                    .scheduledStartTime(ZonedDateTime.now().minusSeconds(30))
                    .parameters(
                            Map.of(
                                    "key", "value"
                            )
                    )
                    .build();
            assertTrue(taskService.registerTask(rq));

            var executor = context.getBean(AsyncTaskExecutorEmbedded.class);
            executor.execute();

            var asyncTaskEntityRepository = context.getBean(AsyncTaskEntityRepository.class);
            var asyncTaskEntity = asyncTaskEntityRepository.findById(taskUid)
                    .orElseThrow(() -> new IllegalArgumentException("Async task not found"));

            assertEquals(AsyncTaskStatus.RETRYING, asyncTaskEntity.getStatus());

            asyncTaskEntity.setLastRetryAt(ZonedDateTime.now().minusMonths(1));

            asyncTaskEntityRepository.saveAndFlush(asyncTaskEntity);

            var cleanUpService = context.getBean(AsyncCleanUpServiceEmbedded.class);
            cleanUpService.execute();

            assertTrue(asyncTaskEntityRepository.existsById(taskUid));
        }
    }
}
