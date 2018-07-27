package ua.shalimov.ioc.context;

import java.util.List;

public interface ApplicationContext {
    <T> T getBean(Class<T> type);

    <T> T getBean(String id, Class<T> type);

    Object getBean(String id);

    List<String> getBeanName();

}
