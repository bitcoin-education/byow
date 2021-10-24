package com.byow.wallet.byow.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Profile("!" + TestLazyBeanInitConfiguration.EAGER_BEAN_INIT)
public class TestLazyBeanInitConfiguration implements BeanFactoryPostProcessor {
    public static final String EAGER_BEAN_INIT = "eager-bean-init";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Arrays.stream(beanFactory.getBeanDefinitionNames())
            .map(beanFactory::getBeanDefinition)
            .forEach(beanDefinition -> beanDefinition.setLazyInit(true));
    }
}
