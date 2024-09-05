package com.github.simple_mocks.async.embedded;

import com.github.simple_mocks.async.embedded.configuration.AsyncServiceEmbeddedConfig;
import com.github.simple_mocks.async.embedded.service.AsyncTaskServiceEmbedded;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * Enabler for implementation of async embedded service.
 *
 * @author sibmaks
 * @see com.github.simple_mocks.async.api.service.AsyncTaskService
 * @see AsyncTaskServiceEmbedded
 * @since 0.0.1
 */
@EnableScheduling
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({AsyncServiceEmbeddedConfig.class})
public @interface EnableAsyncServiceEmbedded {
}
