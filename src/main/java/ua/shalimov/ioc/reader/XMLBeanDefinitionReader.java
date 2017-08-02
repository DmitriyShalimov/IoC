package ua.shalimov.ioc.reader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ua.shalimov.ioc.exception.BeanInstantiationException;
import ua.shalimov.ioc.model.BeanDefinition;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private LinkedList<BeanDefinition> beanDefinitions = new LinkedList<>();

    public List<BeanDefinition> readBeanDefinitions(String path) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(false);
        SAXParser parser;
        InputStream xmlData;
        try {
            xmlData = new FileInputStream(path);
            parser = factory.newSAXParser();
            parser.parse(xmlData, new MyParser());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new BeanInstantiationException(e);
        }
        return beanDefinitions;
    }

    private class MyParser extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("import")) {
                readBeanDefinitions(attributes.getValue("resource"));
            } else {
                BeanDefinition beanDefinition = new BeanDefinition();
                if (qName.equals("bean")) {
                    String id = attributes.getValue("id");
                    checkRepeatableBeanId(id);
                    beanDefinition.setId(id);
                    String fullBeanDefinitionClassName = attributes.getValue("class");
                    String definitionClassName = fullBeanDefinitionClassName.substring(fullBeanDefinitionClassName.lastIndexOf(".") + 1);
                    beanDefinition.setBeanClassName(definitionClassName);
                    beanDefinition.setDependencies(new HashMap<>());
                    beanDefinition.setRefDependencies(new HashMap<>());
                    beanDefinitions.add(beanDefinition);
                }
                if (qName.equals("property")) {
                    String propertyName = attributes.getValue("name");
                    if (attributes.getValue("ref") != null) {
                        beanDefinitions.getLast().getRefDependencies().put(propertyName, attributes.getValue("ref"));
                    } else {
                        beanDefinitions.getLast().getDependencies().put(propertyName, attributes.getValue("value"));
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
