package com.github.sibdevtools.async.embedded.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author sibmaks
 * @since 0.0.9
 */
@Setter
@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("service.async.embedded.flyway")
public class AsyncServiceEmbeddedFlywayProperties {
    private String encoding;
    private String[] locations;
    private String schema;
}
