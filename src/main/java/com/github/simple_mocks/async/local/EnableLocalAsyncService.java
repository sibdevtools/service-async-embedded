package com.github.simple_mocks.async.local;

import com.github.simple_mocks.async.local.conf.LocalAsyncJPASelector;
import com.github.simple_mocks.async.local.conf.LocalAsyncServiceConfig;
import com.github.simple_mocks.async.local.conf.LocalAsyncStarter;
import com.github.simple_mocks.async.local.service.LocalAsyncTaskService;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * Enabler for local implementation of storage service.
 *
 * @author sibmaks
 * @see com.github.simple_mocks.async.api.service.AsyncTaskService
 * @see LocalAsyncTaskService
 * @since 0.0.1
 */
@EnableScheduling
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({LocalAsyncServiceConfig.class, LocalAsyncJPASelector.class, LocalAsyncStarter.class})
public @interface EnableLocalAsyncService {
    String dataSourceBean() default "";
}
