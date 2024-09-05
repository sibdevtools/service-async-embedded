package com.github.simple_mocks.async.embedded.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Setter
@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class AsyncCleanUpServiceProperties {
    private int maxRemovedAtOnce = 32;
    private ChronoUnit taskTTLType = ChronoUnit.DAYS;
    private long taskTTL = 3;
}
