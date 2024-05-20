package com.github.simple_mocks.async.local.conf;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@PropertySource("classpath:async-local-application.properties")
public class LocalAsyncServiceJPASharedConfig {

    @Bean
    @ConfigurationProperties("spring.flyway.local-async")
    public ClassicConfiguration localAsyncFlywayConfiguration(@Qualifier("localAsyncDataSource") DataSource dataSource) {
        var classicConfiguration = new ClassicConfiguration();
        classicConfiguration.setDataSource(dataSource);
        return classicConfiguration;
    }

    @Bean
    public Flyway localAsyncFlyway(@Qualifier("localAsyncFlywayConfiguration") ClassicConfiguration configuration) {
        return new Flyway(configuration);
    }

}
