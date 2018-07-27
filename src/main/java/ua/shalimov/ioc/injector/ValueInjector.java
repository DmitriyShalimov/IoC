package ua.shalimov.ioc.injector;

import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.model.Bean;
import ua.shalimov.ioc.model.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ValueInjector extends Injector {

    public ValueInjector(List<BeanDefinition> beanDefinitions, Map<BeanDefinition, Bean> beanDefinitionToBeanMap, List<Bean> beans) {
        super(beanDefinitions, beanDefinitionToBeanMap, beans);
    }

    @Override
    Object getDependenciesToInject(Map<String, String> dependencies, String fieldName) {
        return dependencies.get(fieldName);
    }

    @Override
    protected Map<String, String> getDependencies(BeanDefinition beanDefinition) {
        return beanDefinition.getDependencies();
    }

    @Override
    void injectPropertyIntoSetter(Object beanValue, Object valueToInject, Method method) {
        String beanPropertyValue = String.valueOf(valueToInject);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> parameterType = parameterTypes[0];
        try {
            if (parameterType.equals(int.class)) {
                method.invoke(beanValue, Integer.parseInt(beanPropertyValue));
            } else if (parameterType.equals(double.class)) {
                method.invoke(beanValue, Double.parseDouble(beanPropertyValue));
            } else if (parameterType.equals(float.class)) {
                method.invoke(beanValue, Float.parseFloat(beanPropertyValue));
            } else if (parameterType.equals(long.class)) {
                method.invoke(beanValue, Long.parseLong(beanPropertyValue));
            } else if (parameterType.equals(short.class)) {
                method.invoke(beanValue, Short.parseShort(beanPropertyValue));
            } else if (parameterType.equals(byte.class)) {
                method.invoke(beanValue, Byte.parseByte(beanPropertyValue));
            } else if (parameterType.equals(boolean.class)) {
                method.invoke(beanValue, Boolean.parseBoolean(beanPropertyValue));
            } else if (parameterType.equals(char.class)) {
                method.invoke(beanValue, beanPropertyValue.charAt(0));
            } else
                method.invoke(beanValue, beanPropertyValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException(e);
        }
    }
}
