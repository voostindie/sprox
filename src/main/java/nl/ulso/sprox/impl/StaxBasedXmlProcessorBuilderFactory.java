package nl.ulso.sprox.impl;

import nl.ulso.sprox.XmlProcessorBuilder;
import nl.ulso.sprox.XmlProcessorBuilderFactory;

/**
 * Implements the {@link XmlProcessorBuilderFactory}; this implementation is registered as a
 * service for the {@link java.util.ServiceLoader}.
 */
public class StaxBasedXmlProcessorBuilderFactory implements XmlProcessorBuilderFactory {
    @Override
    public <T> XmlProcessorBuilder<T> createXmlProcessorBuilder(Class<T> resultClass) {
        return new StaxBasedXmlProcessorBuilder<>(resultClass);
    }
}
