package com.github.sibdevtools.async.embedded;

import com.github.sibdevtools.async.api.entity.AsyncTask;
import com.github.sibdevtools.async.api.rs.AsyncTaskProcessingResult;
import com.github.sibdevtools.async.api.rs.AsyncTaskProcessingResultBuilder;
import com.github.sibdevtools.async.api.service.AsyncTaskProcessor;
import com.github.sibdevtools.async.api.service.AsyncTaskProcessorMeta;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.2
 */
@Component
@AsyncTaskProcessorMeta(taskType = "test-task-retry")
public class TestTaskRetryProcessor implements AsyncTaskProcessor {
    @Nonnull
    @Override
    public AsyncTaskProcessingResult process(@Nonnull AsyncTask asyncTask) {
        return AsyncTaskProcessingResultBuilder.createRetryResult(
                ZonedDateTime.now()
                        .plusDays(1)
        );
    }
}
