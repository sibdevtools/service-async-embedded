package com.github.simple_mocks.async.local.conf;

import com.github.simple_mocks.async.local.EnableLocalAsyncService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author sibmaks
 * @since 0.0.2
 */
public class LocalAsyncJPASelector implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        var attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(
                        EnableLocalAsyncService.class.getName(),
                        false
                )
        );
        if (attributes == null) {
            return;
        }
        var dataSourceBean = attributes.getString("dataSourceBean");

        if (StringUtils.isBlank(dataSourceBean)) {
            var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(LocalAsyncServiceJPAPrivateConfig.class)
                    .setLazyInit(false)
                    .getBeanDefinition();
            registry.registerBeanDefinition("localAsyncServiceJPAPrivateConfig", beanDefinition);
        } else {
            var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(LocalAsyncServiceJPASharedConfig.class)
                    .setLazyInit(false)
                    .getBeanDefinition();
            registry.registerBeanDefinition("localAsyncServiceJPASharedConfig", beanDefinition);
        }
    }

}
