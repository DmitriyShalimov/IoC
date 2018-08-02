package ua.shalimov.ioc.context.beanpostprocessor;


import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.model.Bean;

import java.lang.reflect.Method;
import java.util.List;

public class BeanPostprocessorHandler {
    private List<Bean> beans;

    public BeanPostprocessorHandler(List<Bean> beans) {
        this.beans = beans;
    }

    public void postProcessBeforeInitialization() {
        postProcessInitialization("postProcessBeforeInitialization");
    }

    public void postProcessAfterInitialization() {
        postProcessInitialization("postProcessAfterInitialization");

    }

    private void postProcessInitialization(String methodName) {
        for (Bean bean : beans) {
            Object object = bean.getValue();
            if (BeanPostProcessor.class.isAssignableFrom(object.getClass())) {
                try {
                    Method method = object.getClass().getMethod(methodName, Object.class, String.class);
                    method.invoke(object, object, bean.getId());
                } catch (Exception e) {
                    throw new BeanInstantiationException(e);
                }
            }
        }
    }
}