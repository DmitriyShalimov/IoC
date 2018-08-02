package ua.shalimov.ioc.context;

import ua.shalimov.ioc.context.beanpostprocessor.BeanFactoryPostProcessor;
import ua.shalimov.ioc.context.beanpostprocessor.BeanPostprocessorHandler;
import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.exception.BeanNotFoundException;
import ua.shalimov.ioc.exception.NoUniqueBeanException;
import ua.shalimov.ioc.injector.Injector;
import ua.shalimov.ioc.injector.ReferenceIngector;
import ua.shalimov.ioc.injector.ValueInjector;
import ua.shalimov.ioc.model.Bean;
import ua.shalimov.ioc.model.BeanDefinition;
import ua.shalimov.ioc.reader.BeanDefinitionReader;
import ua.shalimov.ioc.reader.XMLBeanDefinitionReader;

import javax.annotation.PostConstruct;
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
        setBeanDefinitionReader(new XMLBeanDefinitionReader(paths));
        start();
    }

    public ClassPathApplicationContext(String path) {
        this(new String[]{path});
    }

    public void start() {
        beanDefinitions = reader.readBeanDefinitions();
        callBeanFactoryPostProcess(beanDefinitions);
        createBeansFromBeanDefinition();
        injectDependencies();
        BeanPostprocessorHandler beanPostprocessorHendler = new BeanPostprocessorHandler(beans);
        beanPostprocessorHendler.postProcessBeforeInitialization();
        callInitMethod();
        beanPostprocessorHendler.postProcessAfterInitialization();
    }

    private void callBeanFactoryPostProcess(List<BeanDefinition> beanDefinitions) {
        Iterator<BeanDefinition> iterator = beanDefinitions.iterator();
        while (iterator.hasNext()) {
            try {
                BeanDefinition beanDefinition = iterator.next();
                String className = beanDefinition.getBeanClassName();
                Class<?> beanClass = Class.forName(className);
                if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass)) {
                    Bean bean=createBeanFromBeanDefinition(beanDefinition);
                    Method postProcessBeanFactory = beanClass.getMethod("postProcessBeanFactory", List.class);
                    postProcessBeanFactory.invoke(bean.getValue(), beanDefinitions);
                    iterator.remove();
                }
            } catch (Exception e) {
                throw new BeanInstantiationException(e);
            }
        }
    }
    private Bean createBeanFromBeanDefinition(BeanDefinition beanDefinition) {
        try {
            String className = beanDefinition.getBeanClassName();
            Class clazz = Class.forName(className);
            Object obj = clazz.newInstance();
            Bean bean = new Bean();
            bean.setId(beanDefinition.getId());
            bean.setValue(obj);
            return bean;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new BeanInstantiationException(e);
        }
    }

    private void callInitMethod() {
        for (Bean bean : beans) {
            Class<?> beanClass = bean.getValue().getClass();
            for (Method method : beanClass.getMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        method.invoke(bean.getValue());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new BeanInstantiationException(e);
                    }
                }
            }
        }
    }

    public <T> T getBean(Class<T> type) {
        T bean = null;
        int count = 0;
        for (Bean tempBean : beans) {
            if (type.isAssignableFrom(tempBean.getValue().getClass())) {
                bean = type.cast(tempBean.getValue());
                count++;
            }
            if (count > 1) {
                throw new NoUniqueBeanException("Multiple beans found for " + tempBean.getId());
            }
        }
        if (bean == null) {
            throw new BeanNotFoundException("No such bean was registered for class: " + type);
        }
        return bean;
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
            Bean bean = createBeanFromBeanDefinition(beanDefinition);
            beans.add(bean);
            beanDefinitionToBeanMap.put(beanDefinition, bean);
        }
    }



    private void injectDependencies() {
        for (Injector injector : new Injector[]{new ValueInjector(beanDefinitions, beanDefinitionToBeanMap, beans), new ReferenceIngector(beanDefinitions, beanDefinitionToBeanMap, beans)}) {
            injector.injectDependencies();
        }
    }
}
