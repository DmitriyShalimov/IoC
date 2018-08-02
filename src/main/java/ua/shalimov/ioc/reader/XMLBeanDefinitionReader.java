package ua.shalimov.ioc.reader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.model.BeanDefinition;

import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private LinkedList<BeanDefinition> beanDefinitions = new LinkedList<>();
    private final SAXParserFactory factory = SAXParserFactory.newInstance();
    private String[] paths;

    public XMLBeanDefinitionReader(String[] paths) {
        this.paths = paths;
        factory.setValidating(true);
        factory.setNamespaceAware(false);
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        for (String path : paths) {
            beanDefinitions.addAll(readBeanDefinitions(path));
        }
        return beanDefinitions;
    }

    public List<BeanDefinition> readBeanDefinitions(String path) {
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(getClass().getClassLoader().getResourceAsStream(path), new MyParser());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new BeanInstantiationException(e);
        }
        return beanDefinitions;
    }

    private class MyParser extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("import".equals(qName)) {
                readBeanDefinitions(attributes.getValue("resource"));
            } else {
                BeanDefinition beanDefinition = new BeanDefinition();
                if ("bean".equals(qName)) {
                    String id = attributes.getValue("id");
                    checkRepeatableBeanId(id);
                    beanDefinition.setId(id);
                    String definitionClassName = attributes.getValue("class");
                    beanDefinition.setBeanClassName(definitionClassName);
                    beanDefinition.setDependencies(new HashMap<>());
                    beanDefinition.setRefDependencies(new HashMap<>());
                    beanDefinitions.add(beanDefinition);
                } else if ("property".equals(qName)) {
                    String propertyName = attributes.getValue("name");
                    if (attributes.getValue("ref") != null) {
                        Map<String, String> refDependencies = beanDefinitions.getLast().getRefDependencies();
                        refDependencies.put(propertyName, attributes.getValue("ref"));
                    } else {
                        Map<String, String> dependencies = beanDefinitions.getLast().getDependencies();
                        dependencies.put(propertyName, attributes.getValue("value"));
                    }
                }
                super.startElement(uri, localName, qName, attributes);
            }
        }

        private void checkRepeatableBeanId(String id) {
            for (BeanDefinition tempBeanDefinition : beanDefinitions) {
                if (tempBeanDefinition.getId().equals(id)) {
                    throw new BeanInstantiationException("It is forbidden to create two different IDs with the same value");
                }
            }
        }
    }
}
