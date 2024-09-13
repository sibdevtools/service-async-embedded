package com.github.simplemocks.async.embedded;

import com.github.simplemocks.async.api.entity.AsyncTask;
import com.github.simplemocks.async.api.rs.AsyncTaskProcessingResult;
import com.github.simplemocks.async.api.rs.AsyncTaskProcessingResultBuilder;
import com.github.simplemocks.async.api.service.AsyncTaskProcessor;
import com.github.simplemocks.async.api.service.AsyncTaskProcessorMeta;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

/**
 * @author sibmaks
 * @since 0.0.2
 */
@Component
@AsyncTaskProcessorMeta(taskType = "test-task")
public class TestTaskProcessor implements AsyncTaskProcessor {
    @Nonnull
    @Override
    public AsyncTaskProcessingResult process(@Nonnull AsyncTask asyncTask) {
        return AsyncTaskProcessingResultBuilder.createFinishResult();
    }
}
