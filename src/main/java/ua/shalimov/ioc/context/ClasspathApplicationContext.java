package ua.shalimov.ioc.context;


import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.exception.BeanNotFoundException;
import ua.shalimov.ioc.model.Bean;
import ua.shalimov.ioc.model.BeanDefinition;
import ua.shalimov.ioc.reader.BeanDefinitionReader;
import ua.shalimov.ioc.reader.XMLBeanDefinitionReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClasspathApplicationContext implements ApplicationContext {
    private List<Bean> beans;
    private BeanDefinitionReader reader;
    private List<BeanDefinition> beanDefinitions;

    public ClasspathApplicationContext(String[] paths) {
        beans = new ArrayList<>();
        setBeanDefinitionReader(new XMLBeanDefinitionReader());
        for (String path : paths) {
            beanDefinitions = reader.readBeanDefinitions(path);
        }
        createBeansFromBeanDefinition();
        injectRefDependencies();
    }

    public ClasspathApplicationContext(String path) {
        this(new String[]{path});
    }

    public <T> T getBean(Class<T> t) {
        for (Bean bean : beans) {
            if (t.isInstance(bean.getValue())) {
                return t.cast(bean.getValue());
            }
        }
        throw new BeanNotFoundException("No such bean was registered for class: " + t);
    }

    public <T> T getBean(String id, Class<T> t) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id)) {
                return t.cast(bean.getValue());
            }
        }
        throw new BeanNotFoundException("No such bean was registered for class: " + t + " with id: " + id);
    }


    public Object getBean(String id) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id)) {
                return bean.getValue();
            }
        }
        throw new BeanNotFoundException("No such bean was registered with id: " + id);
    }

    public List<String> getBeanName() {
        return beans.stream().map(Bean::getId).collect(Collectors.toList());
    }

    public void setBeanDefinitionReader(BeanDefinitionReader reader) {
        this.reader = reader;
    }

    private void createBeansFromBeanDefinition() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                Class c = Class.forName(beanDefinition.getBeanClassName());
                Object obj = c.newInstance();
                Bean bean = new Bean();
                bean.setId(beanDefinition.getId());
                Method[] methods = c.getMethods();
                for (Method method : methods) {
                    for (String fieldName : beanDefinition.getDependencies().keySet()) {
                        String beanPropertyValue = beanDefinition.getDependencies().get(fieldName);
                        injectPropertyIntoSetter(obj, beanPropertyValue , method, fieldName);
                    }
                }
                bean.setValue(obj);
                beans.add(bean);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new BeanInstantiationException(e);
            }
        }
    }


    private void injectRefDependencies() {
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())) {
                    Class c = bean.getValue().getClass();
                    Method[] methods = c.getMethods();
                    for (Method method : methods) {
                        for (String fieldName : beanDefinition.getRefDependencies().keySet()) {

                            String setterName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
                            if (method.getName().equals(setterName)) {
                                try {
                                    for (Bean tempBean : beans) {
                                        if (tempBean.getId().equals(beanDefinition.getRefDependencies().get(fieldName)))
                                            method.invoke(bean.getValue(), tempBean.getValue());
                                    }
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new BeanInstantiationException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    private void injectPropertyIntoSetter(Object beanValue, String beanPropertyValue, Method method, String fieldName) {
        String setterName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
        if (method.getName().equals(setterName)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> parameterType = parameterTypes[0];
            try {
                if (parameterType.equals(int.class)) {
                    method.invoke(beanValue, Integer.parseInt(beanPropertyValue));
                } else {
                    if (parameterType.equals(double.class)) {
                        method.invoke(beanValue, Double.parseDouble(beanPropertyValue));
                    } else {
                        if (parameterType.equals(float.class)) {
                            method.invoke(beanValue, Float.parseFloat(beanPropertyValue));
                        } else {
                            if (parameterType.equals(long.class)) {
                                method.invoke(beanValue, Long.parseLong(beanPropertyValue));
                            } else {
                                if (parameterType.equals(short.class)) {
                                    method.invoke(beanValue, Short.parseShort(beanPropertyValue));
                                } else {
                                    if (parameterType.equals(byte.class)) {
                                        method.invoke(beanValue, Byte.parseByte(beanPropertyValue));
                                    } else {
                                        if (parameterType.equals(boolean.class)) {
                                            method.invoke(beanValue, Boolean.parseBoolean(beanPropertyValue));
                                        } else {
                                            if (parameterType.equals(char.class)) {
                                                method.invoke(beanValue, beanPropertyValue.charAt(0));
                                            } else {
                                                method.invoke(beanValue, beanPropertyValue);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanInstantiationException(e);
            }
        } else {
            throw new BeanInstantiationException("");
        }
    }
}
