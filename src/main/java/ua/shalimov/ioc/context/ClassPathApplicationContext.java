package ua.shalimov.ioc.context;

import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.exception.BeanNotFoundException;
import ua.shalimov.ioc.injector.Injector;
import ua.shalimov.ioc.injector.ReferenceIngector;
import ua.shalimov.ioc.injector.ValueInjector;
import ua.shalimov.ioc.model.Bean;
import ua.shalimov.ioc.model.BeanDefinition;
import ua.shalimov.ioc.reader.BeanDefinitionReader;
import ua.shalimov.ioc.reader.XMLBeanDefinitionReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ClassPathApplicationContext implements ApplicationContext {
    private List<Bean> beans;
    private BeanDefinitionReader reader;
    private List<BeanDefinition> beanDefinitions;
    private Map<BeanDefinition, Bean> beanDefinitionToBeanMap = new HashMap<>();

    public ClassPathApplicationContext(String... paths) {
        beans = new ArrayList<>();
        setBeanDefinitionReader(new XMLBeanDefinitionReader());
        for (String path : paths) {
            beanDefinitions = reader.readBeanDefinitions(path);
        }
        createBeansFromBeanDefinition();
        injectDependencies();
    }

    public ClassPathApplicationContext(String resourcesName) {
        this(new String[]{resourcesName});
    }

    public <T> T getBean(Class<T> type) {
        for (Bean bean : beans) {
            if (type.isInstance(bean.getValue())) {
                return type.cast(bean.getValue());
            }
        }
        throw new BeanNotFoundException("No such bean was registered for class: " + type);
    }

    public <T> T getBean(String id, Class<T> type) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id)) {
                return type.cast(bean.getValue());
            }
        }
        throw new BeanNotFoundException("No such bean was registered for class: " + type + " with id: " + id);
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
                String className = beanDefinition.getBeanClassName();
                Class clazz = Class.forName(className);
                Object obj = clazz.newInstance();

                Bean bean = new Bean();
                bean.setId(beanDefinition.getId());
                bean.setValue(obj);

                beans.add(bean);
                beanDefinitionToBeanMap.put(beanDefinition, bean);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new BeanInstantiationException(e);
            }
        }
    }

    private void injectDependencies() {
        for (Injector injector : new Injector[]{new ValueInjector(beanDefinitions, beanDefinitionToBeanMap, beans), new ReferenceIngector(beanDefinitions, beanDefinitionToBeanMap, beans)}) {
            injector.injectDependencies();
        }
    }
}
