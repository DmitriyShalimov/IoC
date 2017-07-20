package ioccontainer;

import java.util.ArrayList;
import java.util.List;

public interface ApplicationContext {
    <T> T getBean(Class<T> t);
    <T> T getBean(String name,Class<T> t);
    Object getBean(String name);
    List<String> getBeanName();
    void setBeanDefinitionReader(BeanDefinitionReader reader);
}
