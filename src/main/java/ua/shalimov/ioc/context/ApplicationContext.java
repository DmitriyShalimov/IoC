package ua.shalimov.ioc.context;

import java.util.List;

public interface ApplicationContext {
    <T> T getBean(Class<T> t);

    <T> T getBean(String id, Class<T> t);

    Object getBean(String id);

    List<String> getBeanName();

}
