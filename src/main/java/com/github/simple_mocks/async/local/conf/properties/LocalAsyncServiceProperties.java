package com.github.simple_mocks.async.local.conf.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Setter
@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("service.local.async")
public class LocalAsyncServiceProperties {
    private LocalAsyncCleanUpServiceProperties cleanUp;
    private LocalAsyncExecutorServiceProperties executor;
    private List<String> folders2Create;
}
