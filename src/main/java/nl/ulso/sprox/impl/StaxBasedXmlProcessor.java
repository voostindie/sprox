/*
 * Copyright 2013 Vincent Oostindië
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.sprox.impl;

import nl.ulso.sprox.Parser;
import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.XmlProcessorException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * Default implementation of the {@link nl.ulso.sprox.XmlProcessor} interface on top of the JDKs built-in StAX
 * parser.
 * <p/>
 * On construction, a processor is initialized with an initial list of event handlers, all based on annotated controller
 * methods. When the processor goes through a document, it implements the following algorithm:
 * <p/>
 * <ul>
 * <li>Copy the initial list of event handlers to a new list, specifically for this execution</li>
 * <li>Go through the document, event by event</li>
 * <li>For every event, check if it matches one of the event handlers in the list, from back to front. If so:
 * <ul>
 * <li>Remove the event handler from the list.</li>
 * <li>Let the event handler process the event.</li>
 * <li>Add the resulting event handler to the back of the list, giving it the highest priority.</li>
 * </ul>
 * </li>
 * </ul>
 */
final class StaxBasedXmlProcessor<T> implements XmlProcessor<T> {
    private static final String NAMESPACE_AWARE = "javax.xml.stream.isNamespaceAware";
    private static final String COALESCE_CHARACTERS = "javax.xml.stream.isCoalescing";
    private static final String REPLACE_INTERNAL_ENTITY_REFERENCES = "javax.xml.stream.isReplacingEntityReferences";
    private static final String SUPPORT_EXTERNAL_ENTITIES = "javax.xml.stream.isSupportingExternalEntities";
    private static final String SUPPORT_DTDS = "javax.xml.stream.supportDTD";

    private final Class<T> resultClass;
    private final Map<Class, ControllerProvider> controllerProviders;
    private final XMLInputFactory inputFactory;
    private final List<EventHandler> initialEventHandlers;
    private final Map<Class<?>, Parser<?>> parsers;

    StaxBasedXmlProcessor(Class<T> resultClass, Map<Class, ControllerProvider> controllerProviders,
                          List<EventHandler> eventHandlers, Map<Class<?>, Parser<?>> parsers, boolean useNamespaces) {
        this.resultClass = resultClass;
        this.controllerProviders = unmodifiableMap(new HashMap<>(controllerProviders));
        this.initialEventHandlers = unmodifiableList(new ArrayList<>(eventHandlers));
        this.parsers = unmodifiableMap(new HashMap<>(parsers));
        this.inputFactory = XMLInputFactory.newFactory();
        this.inputFactory.setProperty(NAMESPACE_AWARE, useNamespaces);
        this.inputFactory.setProperty(COALESCE_CHARACTERS, true);
        this.inputFactory.setProperty(REPLACE_INTERNAL_ENTITY_REFERENCES, true);
        this.inputFactory.setProperty(SUPPORT_EXTERNAL_ENTITIES, false);
        this.inputFactory.setProperty(SUPPORT_DTDS, false);
    }

    @Override
    public T execute(Reader reader) throws XmlProcessorException {
        try {
            return processEventReader(inputFactory.createXMLEventReader(reader));
        } catch (XMLStreamException e) {
            throw new XmlProcessorException(e);
        }
    }

    @Override
    public T execute(InputStream inputStream) throws XmlProcessorException {
        try {
            return processEventReader(inputFactory.createXMLEventReader(inputStream));
        } catch (XMLStreamException e) {
            throw new XmlProcessorException(e);
        }
    }

    private T processEventReader(XMLEventReader eventReader) throws XMLStreamException, XmlProcessorException {
        final List<EventHandler> eventHandlers = new ArrayList<>(initialEventHandlers);
        final ExecutionContext<T> context = new ExecutionContext<>(resultClass, provideControllers(), parsers);
        while (eventReader.hasNext()) {
            final XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement()) {
                context.increaseDepth();
            }
            final EventHandler handler = fetchFirstMatchingEventHandler(eventHandlers, event, context);
            if (handler != null) {
                final EventHandler nextEventHandler = handler.process(event, context);
                eventHandlers.add(0, nextEventHandler);
            }
            if (event.isEndElement()) {
                context.decreaseDepth();
            }
        }
        final T result = context.getResult();
        if (result == null && !Void.class.equals(resultClass)) {
            throw new XmlProcessorException("No result collected of type " + resultClass.getName());
        }
        return result;
    }

    private Map<Class, Object> provideControllers() {
        final Map<Class, Object> controllers = new HashMap<>(controllerProviders.size());
        for (Map.Entry<Class, ControllerProvider> entry : controllerProviders.entrySet()) {
            controllers.put(entry.getKey(), entry.getValue().getController());
        }
        return controllers;
    }

    private EventHandler fetchFirstMatchingEventHandler(List<EventHandler> eventHandlers, XMLEvent event,
                                                        ExecutionContext executionContext) {
        final Iterator<EventHandler> iterator = eventHandlers.iterator();
        while (iterator.hasNext()) {
            final EventHandler handler = iterator.next();
            if (handler.matches(event, executionContext)) {
                iterator.remove();
                return handler;
            }
        }
        return null;
    }
}
