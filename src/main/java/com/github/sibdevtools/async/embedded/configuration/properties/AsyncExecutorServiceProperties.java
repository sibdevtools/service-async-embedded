package com.github.sibdevtools.async.embedded.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Setter
@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class AsyncExecutorServiceProperties {
    private int parallelTasks = Runtime.getRuntime().availableProcessors();
    private long rate;

    /**
     * Set parallel task value
     *
     * @param parallelTasks new value
     */
    public void setParallelTasks(int parallelTasks) {
        if (parallelTasks >= 1) {
            this.parallelTasks = parallelTasks;
        }
    }
}
