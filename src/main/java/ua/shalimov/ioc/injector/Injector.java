package ua.shalimov.ioc.injector;

import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.model.Bean;
import ua.shalimov.ioc.model.BeanDefinition;

import java.lang.reflect.Method;
import java.util.*;

public abstract class Injector {
    private List<BeanDefinition> beanDefinitions;
    private Map<BeanDefinition, Bean> beanDefinitionToBeanMap;
    private List<Bean> beans;

    public Injector(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionToBeanMap, List<Bean> beans) {
        this.beanDefinitions = beanDefinitions;
        this.beanDefinitionToBeanMap = beanDefinitionToBeanMap;
        this.beans = beans;
    }

    public List<Bean> getBeans() {
        return beans;
    }

    public void injectDependencies() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = beanDefinitionToBeanMap.get(beanDefinition);
            Map<String, String> dependencies = getDependencies(beanDefinition);
            for (String fieldName : dependencies.keySet()) {
                Method method = getSetterMethod(bean.getValue().getClass(), fieldName);
                Object dependendencyToInject = getDependenciesToInject(dependencies, fieldName);

                injectPropertyIntoSetter(bean.getValue(), dependendencyToInject, method);
            }
        }

    }

    abstract Object getDependenciesToInject(Map<String, String> dependencies, String fieldName);

    protected abstract Map<String, String> getDependencies(BeanDefinition beanDefinition);

    abstract void injectPropertyIntoSetter(Object beanValue, Object beanPropertyValue, Method method);

    private Method getSetterMethod(Class beanClass, String fieldName) {
        String setterName = generateSetterName(fieldName);
        Optional<Method> methodOptional = Arrays.stream(beanClass.getMethods()).filter(s -> s.getName().contains(setterName)).findAny();
        methodOptional.orElseThrow(() -> new BeanInstantiationException("No setter was found in class" + beanClass.getCanonicalName() + " for field " + fieldName));
        return methodOptional.get();
    }

    private String generateSetterName(String fieldName) {
        return "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
    }
}
