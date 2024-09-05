package com.github.simple_mocks.async.embedded.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties("service.embedded.async")
public class AsyncServiceEmbeddedProperties {
    private AsyncCleanUpServiceProperties cleanUp;
    private AsyncExecutorServiceProperties executor;
}
