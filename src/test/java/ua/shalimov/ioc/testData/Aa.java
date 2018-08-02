package ua.shalimov.ioc.testData;

import ua.shalimov.ioc.context.beanpostprocessor.BeanFactoryPostProcessor;
import ua.shalimov.ioc.context.beanpostprocessor.BeanPostProcessor;
import ua.shalimov.ioc.model.BeanDefinition;

import java.util.List;

public class Aa implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(List<BeanDefinition> beanDefinitions) {
        System.out.println("here");
    }
}
