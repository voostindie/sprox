/*
 * Copyright 2013-2014 Vincent Oostindië
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

import nl.ulso.sprox.ControllerFactory;
import nl.ulso.sprox.Namespace;
import nl.ulso.sprox.Namespaces;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Parser;
import nl.ulso.sprox.Recursive;
import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.XmlProcessorBuilder;
import nl.ulso.sprox.parsers.BooleanParser;
import nl.ulso.sprox.parsers.ByteParser;
import nl.ulso.sprox.parsers.CharacterParser;
import nl.ulso.sprox.parsers.DoubleParser;
import nl.ulso.sprox.parsers.FloatParser;
import nl.ulso.sprox.parsers.IntegerParser;
import nl.ulso.sprox.parsers.LongParser;
import nl.ulso.sprox.parsers.ShortParser;
import nl.ulso.sprox.parsers.StringParser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static nl.ulso.sprox.impl.ReflectionUtil.resolveObjectClass;

/**
 * Default {@link nl.ulso.sprox.XmlProcessorBuilder} implementation.
 * <p>
 * Whenever a controller is added, each method in the controller is scanned to see if it is annotated with
 * {@link nl.ulso.sprox.Node}. If so, a {@link StartNodeEventHandler} is created and stored in a list. When building the
 * {@link StaxBasedXmlProcessor}, it gets passed these event handlers.
 */
public final class StaxBasedXmlProcessorBuilder<T> implements XmlProcessorBuilder<T> {
    private static final String PARSER_FROM_STRING_METHOD = Parser.class.getMethods()[0].getName();
    private static final String CONTROLLER_FACTORY_CREATE_METHOD = ControllerFactory.class.getMethods()[0].getName();
    private static final Map<Class<?>, Parser<?>> DEFAULT_PARSERS;

    static {
        DEFAULT_PARSERS = new HashMap<>(9, 1.0f);
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

    /**
     * Creates a default {@link nl.ulso.sprox.XmlProcessorBuilder} for the specified result class.
     *
     * @param resultClass The type the processor must generate; may not be {@code null}
     */
    StaxBasedXmlProcessorBuilder(Class<T> resultClass) {
        this.resultClass = requireNonNull(resultClass);
        this.controllerProviders = new HashMap<>();
        this.eventHandlers = new ArrayList<>();
        this.parsers = new HashMap<>(DEFAULT_PARSERS);
        this.controllersWithNamespaces = 0;
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
        if (controllerFactory.getClass().isSynthetic()) {
            throw new IllegalArgumentException("Unfortunately you cannot pass a lambda to this method. Type " +
                    "information is lost so there's no way to detect the return type. Please use the " +
                    "addControllerFactory(ControllerFactory<?>, Class<?>) method instead.");
        }
        try {
            final Class<?> type = controllerFactory.getClass().getMethod(
                    CONTROLLER_FACTORY_CREATE_METHOD).getReturnType();
            //noinspection unchecked
            return addControllerFactory(controllerFactory, (Class<? super Object>) type);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot resolve controller factory target type from class: "
                    + controllerFactory.getClass() + ". There might be multiple methods with name "
                    + CONTROLLER_FACTORY_CREATE_METHOD + " in the factory class.", e);
        }
    }

    @Override
    public <F> XmlProcessorBuilder<T> addControllerFactory(ControllerFactory<F> controllerFactory,
                                                           Class<? super F> type) {
        requireNonNull(controllerFactory);
        requireNonNull(type);
        processControllerClass(type);
        controllerProviders.put(type, new FactoryBasedControllerProvider(controllerFactory));
        return this;
    }

    private void processControllerClass(Class<?> clazz) {
        if (controllerProviders.containsKey(clazz)) {
            throw new IllegalArgumentException("A controller of this class is already registered: " + clazz);
        }
        if (hasNamespacesDeclared(clazz)) {
            controllersWithNamespaces++;
        }
        final ControllerClass<?> controllerClass = new ControllerClass<>(clazz);
        for (Method method : clazz.getMethods()) {
            if (!method.isAnnotationPresent(Node.class)) {
                continue;
            }
            final boolean recursive = method.isAnnotationPresent(Recursive.class);
            eventHandlers.add(new StartNodeEventHandler(controllerClass, method, recursive));
        }
    }

    private boolean hasNamespacesDeclared(Class<?> controllerClass) {
        return controllerClass.isAnnotationPresent(Namespaces.class)
                || controllerClass.isAnnotationPresent(Namespace.class);
    }

    @Override
    public XmlProcessorBuilder<T> addParser(Parser<?> parser) {
        requireNonNull(parser);
        if (parser.getClass().isSynthetic()) {
            throw new IllegalArgumentException("Unfortunately you cannot pass a lambda to this method. Type " +
                    "information is lost so there's no way to detect the return type. Please use the " +
                    "addParser(Parser<?>, Class<?>) method instead.");
        }
        try {
            final Class<?> type = parser.getClass().getMethod(PARSER_FROM_STRING_METHOD, String.class).getReturnType();
            //noinspection unchecked
            return addParser(parser, (Class<? super Object>) type);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot resolve parser target type from class: " + parser.getClass()
                    + ". There might be multiple methods with name " + PARSER_FROM_STRING_METHOD
                    + " in the parser class. Please use the addParser(Parser<?>, Class<?>) method instead.", e);
        }
    }

    @Override
    public <P> XmlProcessorBuilder<T> addParser(Parser<P> parser, Class<? super P> type) {
        requireNonNull(parser);
        requireNonNull(type);
        parsers.put(resolveObjectClass(type), parser);
        return this;
    }

    @Override
    public XmlProcessor<T> buildXmlProcessor() {
        if (eventHandlers.isEmpty()) {
            throw new IllegalStateException("Cannot build an XmlProcessor. No controllers were added, " +
                    "or the controllers do not have annotated methods. Make sure the controller methods are public.");
        }
        if (controllersWithNamespaces > 0 && controllersWithNamespaces < controllerProviders.size()) {
            throw new IllegalStateException("Cannot build an XmlProcessor. When using namespaces, " +
                    "all controllers must use namespaces.");
        }
        return new StaxBasedXmlProcessor<>(resultClass, controllerProviders, eventHandlers, parsers,
                controllersWithNamespaces > 0);
    }
}
