package com.github.simple_mocks.async.local.conf;

import com.github.simple_mocks.async.local.EnableLocalAsyncService;
import com.github.simple_mocks.async.local.repository.AsyncTaskEntityRepository;
import com.github.simple_mocks.async.local.repository.AsyncTaskParamEntityRepository;
import com.github.simple_mocks.async.local.service.*;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class LocalAsyncServiceConfig {

    @Bean
    public LocalAsyncTaskProcessorRegistry localAsyncTaskProcessorRegistry() {
        return new LocalAsyncTaskProcessorRegistry();
    }

    @Bean
    public LocalAsyncTaskExecutor localAsyncTaskExecutor(LocalAsyncTaskProcessorRegistry registry,
                                                         AsyncTaskEntityRepository repository,
                                                         AsyncTaskParamEntityRepository paramERepository,
                                                         LocalAsyncServiceProperties properties,
                                                         ExecutorService executorService) {
        return new LocalAsyncTaskExecutor(registry, repository, paramERepository, properties, executorService);
    }

    @Bean
    public LocalAsyncTaskService localAsyncTaskService(AsyncTaskEntityRepository asyncTaskEntityRepository,
                                                       AsyncTaskParamEntityRepository asyncTaskParamEntityRepository) {
        return new LocalAsyncTaskService(asyncTaskEntityRepository, asyncTaskParamEntityRepository);
    }

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
        var flyway = new Flyway(configuration);
        flyway.migrate();
        return flyway;
    }

    @Bean
    public TaskScheduler localAsyncScheduledExecutor() {
        return new ThreadPoolTaskSchedulerBuilder()
                .threadNamePrefix("localAsyncExecutor-")
                .poolSize(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Bean
    public ExecutorService localAsyncAsyncTaskExecutor(LocalAsyncServiceProperties properties) {
        var propertiesExecutor = properties.getExecutor();
        return Executors.newFixedThreadPool(propertiesExecutor.getParallelTasks());
    }

    @Bean
    public LocalAsyncServiceContextListener localAsyncServiceContextListener(LocalAsyncTaskProcessorRegistry registry) {
        return new LocalAsyncServiceContextListener(registry);
    }

    @Bean
    public LocalAsyncCleanUpService localAsyncCleanUpService(
            LocalAsyncServiceProperties properties,
            AsyncTaskParamEntityRepository asyncTaskParamEntityRepository,
            AsyncTaskEntityRepository asyncTaskEntityRepository) {
        return new LocalAsyncCleanUpService(properties, asyncTaskParamEntityRepository, asyncTaskEntityRepository);
    }
}
