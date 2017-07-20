package ioccontainer;

import java.util.List;

public interface BeanDefinitionReader {
   List<BeanDefinition> readBeanDefinitions(String path);

}
