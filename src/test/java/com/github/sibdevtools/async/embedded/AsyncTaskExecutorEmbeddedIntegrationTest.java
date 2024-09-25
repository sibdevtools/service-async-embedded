package com.github.sibdevtools.async.embedded;

import com.github.sibdevtools.async.api.rq.CreateAsyncTaskRq;
import com.github.sibdevtools.async.embedded.entity.AsyncTaskStatus;
import com.github.sibdevtools.async.embedded.repository.AsyncTaskEntityRepository;
import com.github.sibdevtools.async.embedded.service.AsyncCleanUpServiceEmbedded;
import com.github.sibdevtools.async.embedded.service.AsyncTaskExecutorEmbedded;
import com.github.sibdevtools.async.embedded.service.AsyncTaskServiceEmbedded;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ActiveProfiles("startup-test")
@SpringBootTest
class AsyncTaskExecutorEmbeddedIntegrationTest {
    @Autowired
    private AsyncTaskServiceEmbedded taskService;
    @Autowired
    private AsyncTaskExecutorEmbedded executor;
    @Autowired
    private AsyncCleanUpServiceEmbedded cleanUpService;
    @Autowired
    private AsyncTaskEntityRepository asyncTaskEntityRepository;

    @Test
    void testExecuteInvalidVersion() {

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
        var rs = taskService.registerTask(rq);

        assertTrue(rs.getBody());

        executor.execute();

        var asyncTaskEntity = asyncTaskEntityRepository.findById(taskUid)
                .orElseThrow(() -> new IllegalArgumentException("Async task not found"));

        assertEquals(AsyncTaskStatus.FAILED, asyncTaskEntity.getStatus());

        asyncTaskEntity.setLastRetryAt(ZonedDateTime.now().minusMonths(1));

        asyncTaskEntityRepository.saveAndFlush(asyncTaskEntity);

        cleanUpService.execute();

        //Flicking
//            assertFalse(asyncTaskEntityRepository.existsById(taskUid));
    }

    @Test
    void testExecute() {

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
        var rs = taskService.registerTask(rq);

        assertTrue(rs.getBody());

        executor.execute();

        var asyncTaskEntity = asyncTaskEntityRepository.findById(taskUid)
                .orElseThrow(() -> new IllegalArgumentException("Async task not found"));

        assertEquals(AsyncTaskStatus.COMPLETED, asyncTaskEntity.getStatus());

        asyncTaskEntity.setLastRetryAt(ZonedDateTime.now().minusMonths(1));

        asyncTaskEntityRepository.saveAndFlush(asyncTaskEntity);

        cleanUpService.execute();

        //Flicking
//            assertFalse(asyncTaskEntityRepository.existsById(taskUid));
    }

    @Test
    void testExecuteRetry() {
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
        var rs = taskService.registerTask(rq);

        assertTrue(rs.getBody());

        executor.execute();

        var asyncTaskEntity = asyncTaskEntityRepository.findById(taskUid)
                .orElseThrow(() -> new IllegalArgumentException("Async task not found"));

        assertEquals(AsyncTaskStatus.RETRYING, asyncTaskEntity.getStatus());

        asyncTaskEntity.setLastRetryAt(ZonedDateTime.now().minusMonths(1));

        asyncTaskEntityRepository.saveAndFlush(asyncTaskEntity);

        cleanUpService.execute();

        //Flicking
//            assertTrue(asyncTaskEntityRepository.existsById(taskUid));
    }
}
