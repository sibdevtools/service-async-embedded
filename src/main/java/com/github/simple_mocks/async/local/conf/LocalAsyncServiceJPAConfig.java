package com.github.simple_mocks.async.local.conf;

import com.github.simple_mocks.async.local.EnableLocalAsyncService;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@PropertySource("classpath:async-local-application.properties")
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackageClasses = {EnableLocalAsyncService.class},
        entityManagerFactoryRef = "localAsyncEntityManagerFactory",
        transactionManagerRef = "localAsyncTransactionManager"
)
public class LocalAsyncServiceJPAConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.local-async")
    public DataSourceProperties localAsyncDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource localAsyncDataSource(
            @Qualifier("localAsyncDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.jpa.local-async.properties")
    public Map<String, String> localAsyncJpaProperties() {
        return new HashMap<>();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localAsyncEntityManagerFactory(
            @Qualifier("localAsyncDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder managerFactoryBuilder,
            @Qualifier("localAsyncJpaProperties") Map<String, String> localAsyncJpaProperties) {
        return managerFactoryBuilder
                .dataSource(dataSource)
                .packages(EnableLocalAsyncService.class)
                .properties(localAsyncJpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager localAsyncTransactionManager(
            @Qualifier("localAsyncEntityManagerFactory") LocalContainerEntityManagerFactoryBean managerFactoryBean) {
        var entityManagerFactory = managerFactoryBean.getObject();
        Objects.requireNonNull(entityManagerFactory);
        return new JpaTransactionManager(entityManagerFactory);
    }

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
