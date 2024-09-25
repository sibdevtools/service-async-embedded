package com.github.sibdevtools.async.embedded.service;

import com.github.sibdevtools.async.api.service.AsyncTaskProcessor;
import com.github.sibdevtools.async.api.service.AsyncTaskProcessorMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Service
@ConditionalOnProperty(name = "service.async.mode", havingValue = "EMBEDDED")
public class AsyncServiceContextListener implements ApplicationListener<ContextRefreshedEvent> {
    private final AsyncTaskProcessorRegistryEmbedded processorRegistry;

    @Autowired
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
