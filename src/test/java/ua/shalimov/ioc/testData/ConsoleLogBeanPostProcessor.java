package ua.shalimov.ioc.testData;

import ua.shalimov.ioc.context.beanpostprocessor.BeanPostProcessor;

public class ConsoleLogBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("Bean with id=" + beanName + " before init");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("Bean with id=" + beanName + "after init");
        return bean;
    }
}
