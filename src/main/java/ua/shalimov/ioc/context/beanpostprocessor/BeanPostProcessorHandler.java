package ua.shalimov.ioc.context.beanpostprocessor;


import ua.shalimov.ioc.model.Bean;

import java.util.List;

public class BeanPostProcessorHandler {
    private List<Bean> beans;

    public BeanPostProcessorHandler(List<Bean> beans) {
        this.beans = beans;
    }

    public void postProcessBeforeInitialization() {
        postProcessInitialization(true);
    }

    public void postProcessAfterInitialization() {
       postProcessInitialization(false);
    }

    public void postProcessInitialization(boolean beforeInitialization) {
        for (Bean bean : beans) {
            Object object = bean.getValue();
            if (BeanPostProcessor.class.isAssignableFrom(object.getClass())) {
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) object;
                for (Bean beanForPostProcess : beans) {
                    Object newValue;
                    if (beforeInitialization) {
                        newValue = beanPostProcessor.postProcessBeforeInitialization(beanForPostProcess.getValue(), beanForPostProcess.getId());
                    } else {
                        newValue = beanPostProcessor.postProcessAfterInitialization(beanForPostProcess, beanForPostProcess.getId());
                    }
                    beanForPostProcess.setValue(newValue);
                }
            }
        }
    }
}