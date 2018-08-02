package ua.shalimov.ioc.reader;

import ua.shalimov.ioc.model.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions();
}
