package ua.shalimov.ioc.reader;

import org.junit.Test;
import ua.shalimov.ioc.model.BeanDefinition;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class XMLBeanDefinitionReaderTest {
    @Test
    public void readBeanDefinitionsTest() {
        List<BeanDefinition> beanDefinitions;
        String[]paths= new String[1];
        paths[0]="context.xml";
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader(paths);
        beanDefinitions = beanDefinitionReader.readBeanDefinitions();
        assertThat(beanDefinitions.size(), is(4));
        assertThat(beanDefinitions.get(0).getId(), is("mailService"));
        assertThat(beanDefinitions.get(0).getBeanClassName(), is("ua.shalimov.ioc.testData.MailService"));
        assertThat(beanDefinitions.get(0).getDependencies().get("port"), is("3000"));
        assertThat(beanDefinitions.get(1).getRefDependencies().get("mailService"), is("mailService"));
    }

}