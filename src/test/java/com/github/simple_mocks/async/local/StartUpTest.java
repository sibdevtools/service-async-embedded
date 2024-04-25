package com.github.simple_mocks.async.local;

import com.github.simple_mocks.async.api.rq.CreateAsyncTaskRq;
import com.github.simple_mocks.async.local.entity.AsyncTaskStatus;
import com.github.simple_mocks.async.local.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.local.service.LocalAsyncCleanUpService;
import com.github.simple_mocks.async.local.service.LocalAsyncTaskExecutor;
import com.github.simple_mocks.async.local.service.LocalAsyncTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@EnableLocalAsyncService
@SpringBootApplication
class StartUpTest {
    @Test
    void testStartUp() {
        try (var context = SpringApplication.run(StartUpTest.class)) {
            assertNotNull(context);

            var taskService = context.getBean(LocalAsyncTaskService.class);

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

            var executor = context.getBean(LocalAsyncTaskExecutor.class);
            executor.execute();

            var asyncTaskEntityRepository = context.getBean(AsyncTaskEntityRepository.class);
            var asyncTaskEntity = asyncTaskEntityRepository.findById(taskUid)
                    .orElseThrow(() -> new IllegalArgumentException("Async task not found"));

            assertEquals(AsyncTaskStatus.FAILED, asyncTaskEntity.getStatus());

            asyncTaskEntity.setLastRetryAt(ZonedDateTime.now().minusMonths(1));

            asyncTaskEntityRepository.save(asyncTaskEntity);

            var cleanUpService = context.getBean(LocalAsyncCleanUpService.class);
            cleanUpService.execute();

            assertFalse(asyncTaskEntityRepository.existsById(taskUid));
        }
    }
}
