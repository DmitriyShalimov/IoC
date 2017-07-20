package ioccontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClasspathApplicationContext implements ApplicationContext {
    private List<Bean> beans = new ArrayList<>();
    private BeanDefinitionReader reader;
    private List<BeanDefinition> beanDefinitions;

    public ClasspathApplicationContext(String path) {
        setBeanDefinitionReader(new XMLBeanDefinitionReader());
        beanDefinitions = reader.readBeanDefinitions(path);
        createBeansFromBeanDefinition();
        injectDependencies();
        injectRefDependencies();
    }

    public <T> T getBean(Class<T> t) {
        for (Bean bean : beans) {
            if (bean.getValue().getClass().getSimpleName().equals(t.getSimpleName())) {
                return (T) bean.getValue();
            }
        }
        return null;
    }

    public <T> T getBean(String name, Class<T> t) {
        for (Bean bean : beans) {
            if ((bean.getValue().getClass().getSimpleName().equals(t.getSimpleName())) && (bean.getId().equals(name))) {
                return (T) bean.getValue();
            }
        }
        return null;
    }


    public Object getBean(String name) {
        for (Bean bean : beans) {
            if (bean.getId().equals(name)) {
                return bean.getValue();
            }
        }
        return null;
    }

    public List<String> getBeanName() {
        return null;
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
                bean.setValue(obj);
                beans.add(bean);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException("Error occurred while creating bean", e);
            }
        }
    }

    private void injectDependencies() {
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())) {
                    Class c = bean.getValue().getClass();
                    Method[] methods = c.getMethods();
                    for (Method method : methods) {
                        for (String fieldName : beanDefinition.getDependencies().keySet()) {
                            String methodName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
                            if (method.getName().equals(methodName)) {
                                Class<?>[] parameterTypes = method.getParameterTypes();
                                try {
                                    if (parameterTypes[0].equals(int.class)) {
                                        method.invoke(bean.getValue(), Integer.parseInt(beanDefinition.getDependencies().get(fieldName)));
                                    } else {
                                        if (parameterTypes[0].equals(double.class)) {
                                            method.invoke(bean.getValue(), Double.parseDouble(beanDefinition.getDependencies().get(fieldName)));
                                        } else {
                                            if (parameterTypes[0].equals(float.class)) {
                                                method.invoke(bean.getValue(), Float.parseFloat(beanDefinition.getDependencies().get(fieldName)));
                                            } else {
                                                if (parameterTypes[0].equals(long.class)) {
                                                    method.invoke(bean.getValue(), Long.parseLong(beanDefinition.getDependencies().get(fieldName)));
                                                } else {
                                                    if (parameterTypes[0].equals(short.class)) {
                                                        method.invoke(bean.getValue(), Short.parseShort(beanDefinition.getDependencies().get(fieldName)));
                                                    } else {
                                                        if (parameterTypes[0].equals(byte.class)) {
                                                            method.invoke(bean.getValue(), Byte.parseByte(beanDefinition.getDependencies().get(fieldName)));
                                                        } else {
                                                            if (parameterTypes[0].equals(boolean.class)) {
                                                                method.invoke(bean.getValue(), Boolean.parseBoolean(beanDefinition.getDependencies().get(fieldName)));
                                                            } else {
                                                                method.invoke(bean.getValue(), beanDefinition.getDependencies().get(fieldName));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException("Error occurred while creating bean", e);
                                }
                            }
                        }

                    }
                }
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
                            String methodName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
                            if (method.getName().equals(methodName)) {
                                try {
                                    for (Bean tempBean : beans) {
                                        if (tempBean.getId().equals(beanDefinition.getRefDependencies().get(fieldName)))
                                            method.invoke(bean.getValue(), tempBean.getValue());
                                    }
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException("Error occurred while creating bean", e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
