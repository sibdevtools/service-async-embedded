package com.github.simple_mocks.async.local.service;

import com.github.simple_mocks.async.api.service.AsyncTaskProcessor;
import com.github.simple_mocks.async.api.service.AsyncTaskProcessorMeta;
import com.github.simple_mocks.async.local.service.LocalAsyncTaskProcessorRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class LocalAsyncServiceContextListener implements ApplicationListener<ContextRefreshedEvent> {
    private final LocalAsyncTaskProcessorRegistry processorRegistry;

    public LocalAsyncServiceContextListener(LocalAsyncTaskProcessorRegistry processorRegistry) {
        this.processorRegistry = processorRegistry;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        var applicationContext = event.getApplicationContext();
        var beans = applicationContext.getBeansWithAnnotation(AsyncTaskProcessorMeta.class);
        for (var entry : beans.entrySet()) {
            var beanName = entry.getKey();
            var meta = applicationContext.findAnnotationOnBean(beanName, AsyncTaskProcessorMeta.class);
            var processor = (AsyncTaskProcessor) entry.getValue();
            processorRegistry.register(meta, processor);
        }
    }
}
