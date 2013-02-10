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

import nl.ulso.sprox.*;
import nl.ulso.sprox.parsers.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static nl.ulso.sprox.impl.ObjectClasses.resolveObjectClass;
import static nl.ulso.sprox.impl.StartNodeEventHandler.createStartNodeEventHandler;

/**
 * Default {@link nl.ulso.sprox.XmlProcessorBuilder} implementation.
 * <p/>
 * Whenever a controller is added, each methods in the controller is scanned to see if it is annotated with
 * {@link nl.ulso.sprox.Node}. If so, a {@link StartNodeEventHandler} is created and stored in a list. When building the
 * {@link StaxBasedXmlProcessor}, it gets passed these event handlers.
 */
public final class StaxBasedXmlProcessorBuilder<T> implements XmlProcessorBuilder<T> {
    private static final String PARSER_FROM_STRING_METHOD = Parser.class.getMethods()[0].getName();
    private static final String CONTROLLER_FACTORY_CREATE_METHOD = ControllerFactory.class.getMethods()[0].getName();
    private static final Map<Class<?>, Parser<?>> DEFAULT_PARSERS;

    static {
        DEFAULT_PARSERS = new HashMap<>(9);
        DEFAULT_PARSERS.put(Boolean.TYPE, new BooleanParser());
        DEFAULT_PARSERS.put(Byte.TYPE, new ByteParser());
        DEFAULT_PARSERS.put(Character.TYPE, new CharacterParser());
        DEFAULT_PARSERS.put(Double.TYPE, new DoubleParser());
        DEFAULT_PARSERS.put(Float.TYPE, new FloatParser());
        DEFAULT_PARSERS.put(Integer.TYPE, new IntegerParser());
        DEFAULT_PARSERS.put(Long.TYPE, new LongParser());
        DEFAULT_PARSERS.put(String.class, new StringParser());
        DEFAULT_PARSERS.put(Short.TYPE, new ShortParser());
    }

    private final Class<T> resultClass;
    private final Map<Class, ControllerProvider> controllerProviders;
    private final List<EventHandler> eventHandlers;
    private final Map<Class<?>, Parser<?>> parsers;
    private int controllersWithNamespaces;

    private StaxBasedXmlProcessorBuilder(Class<T> resultClass) {
        this.resultClass = requireNonNull(resultClass);
        this.controllerProviders = new HashMap<>();
        this.eventHandlers = new ArrayList<>();
        this.parsers = new HashMap<>(DEFAULT_PARSERS);
        this.controllersWithNamespaces = 0;
    }

    /**
     * Creates a default {@link nl.ulso.sprox.XmlProcessorBuilder} for the specified result class.
     *
     * @param resultClass The type the processor must generate; may not be {@code null}
     * @return a new builder; never {@code null}.
     */
    public static <T> XmlProcessorBuilder<T> createBuilder(Class<T> resultClass) {
        return new StaxBasedXmlProcessorBuilder<>(resultClass);
    }

    @Override
    public XmlProcessorBuilder<T> addControllerObject(Object controller) {
        requireNonNull(controller);
        final Class<?> controllerClass = controller.getClass();
        processControllerClass(controllerClass);
        controllerProviders.put(controllerClass, new SingletonControllerProvider(controller));
        return this;
    }

    @Override
    public XmlProcessorBuilder<T> addControllerClass(Class controllerClass) {
        requireNonNull(controllerClass);
        processControllerClass(controllerClass);
        controllerProviders.put(controllerClass, new ClassInstantiationControllerProvider(controllerClass));
        return this;
    }

    @Override
    public XmlProcessorBuilder<T> addControllerFactory(ControllerFactory<?> controllerFactory) {
        requireNonNull(controllerFactory);
        try {
            final Class<?> type = controllerFactory.getClass().getMethod(CONTROLLER_FACTORY_CREATE_METHOD).getReturnType();
            processControllerClass(type);
            controllerProviders.put(type, new FactoryBasedControllerProvider(controllerFactory));
        } catch (NoSuchMethodException e) {
            throw new XmlProcessorException("Cannot resolve controller factory target type from class: "
                    + controllerFactory.getClass(), e);
        }
        return this;
    }

    private void processControllerClass(Class<?> controllerClass) {
        if (controllerProviders.containsKey(controllerClass)) {
            throw new XmlProcessorException("A controller of this class is already registered: " + controllerClass);
        }
        if (controllerClass.getAnnotation(Namespace.class) != null) {
            controllersWithNamespaces++;
        }
        for (Method method : controllerClass.getMethods()) {
            if (!method.isAnnotationPresent(Node.class)) {
                continue;
            }
            eventHandlers.add(createStartNodeEventHandler(controllerClass, method));
        }
    }

    @Override
    public XmlProcessorBuilder<T> addParser(Parser<?> parser) {
        requireNonNull(parser);
        try {
            final Class<?> type = parser.getClass().getMethod(PARSER_FROM_STRING_METHOD, String.class).getReturnType();
            parsers.put(resolveObjectClass(type), parser);
        } catch (NoSuchMethodException e) {
            throw new XmlProcessorException("Cannot resolve parser target type from class: " + parser.getClass(), e);
        }
        return this;
    }

    @Override
    public XmlProcessor<T> buildXmlProcessor() {
        if (eventHandlers.isEmpty()) {
            throw new IllegalStateException("Cannot build an XmlProcessor. No controllers were added, " +
                    "or the controllers do not have annotated methods.");
        }
        if (controllersWithNamespaces > 0 && controllersWithNamespaces < controllerProviders.size()) {
            throw new IllegalStateException("Cannot build an XmlProcessor. When using namespaces, " +
                    "ALL controllers must use namespaces.");
        }
        return new StaxBasedXmlProcessor<>(resultClass, controllerProviders, eventHandlers, parsers,
                controllersWithNamespaces > 0);
    }
}