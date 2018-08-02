package ua.shalimov.ioc.context.beanpostprocessor;

import ua.shalimov.ioc.model.BeanDefinition;

import java.util.List;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(List<BeanDefinition> beanDefinitions);
}
