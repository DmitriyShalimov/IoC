package ua.shalimov.ioc.injector;

import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.model.Bean;
import ua.shalimov.ioc.model.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ReferenceInjector extends Injector {

    public ReferenceInjector(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionToBeanMap, List<Bean> beans) {
        super(beanDefinitions, beanDefinitionToBeanMap, beans);
    }

    @Override
    Object getDependenciesToInject(Map<String, String> dependencies, String fieldName) {
        String referenceId = dependencies.get(fieldName);
        return getBeans().stream().filter(s -> s.getId().equals(referenceId)).findFirst().get().getValue();
    }

    @Override
    protected Map<String, String> getDependencies(BeanDefinition beanDefinition) {
        return beanDefinition.getRefDependencies();
    }

    @Override
    void injectPropertyIntoSetter(Object beanValue, Object valueToInject, Method method) {
        try {
            method.invoke(beanValue, valueToInject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException(e);
        }
    }
}
