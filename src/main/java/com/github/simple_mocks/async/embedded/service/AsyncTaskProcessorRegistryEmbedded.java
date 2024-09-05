package com.github.simple_mocks.async.embedded.service;

import com.github.simple_mocks.async.api.service.AsyncTaskProcessor;
import com.github.simple_mocks.async.api.service.AsyncTaskProcessorMeta;
import com.github.simple_mocks.async.embedded.exception.UnexpectedServiceError;
import com.github.simple_mocks.error_service.exception.ServiceException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class AsyncTaskProcessorRegistryEmbedded {
    private final Map<String, AsyncTaskProcessor> unVersionedTaskProcessors;
    private final Map<String, Map<String, AsyncTaskProcessor>> versionedTaskProcessors;

    public AsyncTaskProcessorRegistryEmbedded() {
        this.unVersionedTaskProcessors = new ConcurrentHashMap<>();
        this.versionedTaskProcessors = new ConcurrentHashMap<>();
    }

    /**
     * Get async task processor
     *
     * @param type    task type
     * @param version task version
     * @return instance of {@link AsyncTaskProcessor}
     * @throws ServiceException in case if processor not found
     */
    public AsyncTaskProcessor getProcessor(String type, String version) {
        var processor = Optional.ofNullable(versionedTaskProcessors.get(type))
                .map(it -> it.get(version))
                .orElseGet(() -> unVersionedTaskProcessors.get(type));
        if (processor == null) {
            throw new UnexpectedServiceError("No processor for task: %s#%s".formatted(type, version));
        }
        return processor;
    }

    /**
     * Register async task processor
     *
     * @param asyncTaskProcessorMeta task processor meta information
     * @param processor              processor instance
     */
    public void register(AsyncTaskProcessorMeta asyncTaskProcessorMeta, AsyncTaskProcessor processor) {
        var type = asyncTaskProcessorMeta.taskType();
        var versions = asyncTaskProcessorMeta.taskVersions();
        if (versions == null || versions.length == 0) {
            if (unVersionedTaskProcessors.putIfAbsent(type, processor) != null) {
                throw new UnexpectedServiceError("UnVersion processor for task %s already exists".formatted(type));
            }
        } else {
            var taskProcessors = versionedTaskProcessors.computeIfAbsent(
                    type,
                    it -> new ConcurrentHashMap<>()
            );
            for (var version : versions) {
                if (taskProcessors.putIfAbsent(version, processor) != null) {
                    throw new UnexpectedServiceError(
                            "Version processor for task %s#%s already exists".formatted(type, version)
                    );
                }
            }
        }
    }
}
