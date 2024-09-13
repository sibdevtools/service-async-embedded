package com.github.simplemocks.async.embedded.service;

import com.github.simplemocks.async.api.service.AsyncTaskProcessor;
import com.github.simplemocks.async.api.service.AsyncTaskProcessorMeta;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class AsyncServiceContextListener implements ApplicationListener<ContextRefreshedEvent> {
    private final AsyncTaskProcessorRegistryEmbedded processorRegistry;

    public AsyncServiceContextListener(AsyncTaskProcessorRegistryEmbedded processorRegistry) {
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
