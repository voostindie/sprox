package nl.ulso.sprox.impl;

import nl.ulso.sprox.*;
import nl.ulso.sprox.parsers.*;
import nl.ulso.sprox.resolvers.DefaultElementNameResolver;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static nl.ulso.sprox.impl.ReflectionUtil.*;

/**
 * Default {@link nl.ulso.sprox.XmlProcessorBuilder} implementation.
 * <p>
 * Whenever a controller is added, each method in the controller is scanned to see if it is annotated with
 * {@link nl.ulso.sprox.Node}. If so, a {@link StartNodeEventHandler} is created and stored in a list. When building
 * the
 * {@link StaxBasedXmlProcessor}, it gets passed these event handlers.
 */
public final class StaxBasedXmlProcessorBuilder<T> implements XmlProcessorBuilder<T> {
    private static final String NAMESPACE_AWARE = "javax.xml.stream.isNamespaceAware";
    private static final String COALESCE_CHARACTERS = "javax.xml.stream.isCoalescing";
    private static final String REPLACE_INTERNAL_ENTITY_REFERENCES = "javax.xml.stream.isReplacingEntityReferences";
    private static final String SUPPORT_EXTERNAL_ENTITIES = "javax.xml.stream.isSupportingExternalEntities";
    private static final String SUPPORT_DTDS = "javax.xml.stream.supportDTD";

    private static final String PARSER_FROM_STRING_METHOD = Parser.class.getMethods()[0].getName();
    private static final String CONTROLLER_FACTORY_CREATE_METHOD = ControllerFactory.class.getMethods()[0].getName();
    private static final ElementNameResolver DEFAULT_RESOLVER = new DefaultElementNameResolver();
    private static final Map<Class<?>, Parser<?>> DEFAULT_PARSERS = Map.of(
            Boolean.TYPE, new BooleanParser(),
            Byte.TYPE, new ByteParser(),
            Character.TYPE, new CharacterParser(),
            Double.TYPE, new DoubleParser(),
            Float.TYPE, new FloatParser(),
            Integer.TYPE, new IntegerParser(),
            Long.TYPE, new LongParser(),
            String.class, new StringParser(),
            Short.TYPE, new ShortParser()
    );

    private final Class<T> resultClass;
    private final Map<Class, ControllerProvider> controllerProviders;
    private final List<EventHandler> eventHandlers;
    private ElementNameResolver resolver;
    private final Map<Class<?>, Parser<?>> parsers;
    private int controllersWithNamespaces;
    private XMLInputFactory inputFactory;

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
        this.resolver = DEFAULT_RESOLVER;
        this.inputFactory = null;
        this.controllersWithNamespaces = 0;
    }

    @Override
    public XmlProcessorBuilder<T> setElementNameResolver(ElementNameResolver resolver) {
        this.resolver = requireNonNull(resolver);
        return this;
    }

    @Override
    public XmlProcessorBuilder<T> resetElementNameResolver() {
        this.resolver = DEFAULT_RESOLVER;
        return this;
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
            throw new IllegalArgumentException("Unfortunately you cannot pass a lambda or method reference to this " +
                    "method. Type information is lost so there's no way to detect the return type. Please use the " +
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

    private void processControllerClass(Class<?> controllerClass) {
        if (controllerProviders.containsKey(controllerClass)) {
            throw new IllegalArgumentException("A controller of this class is already registered: " + controllerClass);
        }
        final NamespaceMap namespaceMap = new NamespaceMap(controllerClass);
        if (namespaceMap.hasNamespacesDeclared()) {
            controllersWithNamespaces++;
        }
        stream(controllerClass.getMethods())
                .filter(method -> method.isAnnotationPresent(Node.class))
                .forEach(method -> eventHandlers.add(new StartNodeEventHandler(
                        createControllerMethod(controllerClass, method, namespaceMap),
                        method.isAnnotationPresent(Recursive.class))));
    }

    private ControllerMethod createControllerMethod(Class<?> controllerClass, Method method, NamespaceMap namespaceMap) {
        final QNameResolver qNameResolver = new QNameResolver(controllerClass, method, namespaceMap, resolver);
        final QName ownerName = qNameResolver.createQName(method.getAnnotation(Node.class).value());
        final List<ControllerParameter> controllerParameters = stream(method.getParameters())
                .map(parameter -> createControllerParameter(parameter, ownerName, qNameResolver))
                .collect(Collectors.toList());
        return new ControllerMethod(controllerClass, method, ownerName, controllerParameters);
    }

    private ControllerParameter createControllerParameter(Parameter parameter, QName ownerName, QNameResolver resolver) {
        if (parameter.isAnnotationPresent(Attribute.class)) {
            return createAttributeControllerParameter(parameter, ownerName, resolver);
        } else if (parameter.isAnnotationPresent(Node.class)) {
            return createNodeControllerParameter(parameter, ownerName, resolver);
        }

        final Type type = parameter.getParameterizedType();
        final boolean optional = isOptionalType(type);
        final Type parameterType = optional ? extractTypeFromOptional(type) : type;

        if (isListType(parameterType)) {
            return createListControllerParameter(parameter, ownerName, resolver);
        }
        return createObjectControllerParameter(parameter, ownerName, resolver);
    }

    private ControllerParameter createAttributeControllerParameter(Parameter parameter, QName ownerName, QNameResolver resolver) {
        final String attribute = parameter.getAnnotation(Attribute.class).value();
        final QName name = resolver.createQName(attribute, parameter, ownerName);
        final Type type = parameter.getParameterizedType();
        return new AttributeControllerParameter(name, resolveObjectClass(type), isOptionalType(type));
    }

    private ControllerParameter createNodeControllerParameter(Parameter parameter, QName ownerName, QNameResolver resolver) {
        final String node = parameter.getAnnotation(Node.class).value();
        final QName name = resolver.createQName(node, parameter, ownerName);
        final Type type = parameter.getParameterizedType();
        return new NodeControllerParameter(ownerName, name, resolveObjectClass(type), isOptionalType(type));
    }

    private ControllerParameter createListControllerParameter(Parameter parameter, QName ownerName, QNameResolver resolver) {
        final Type type = parameter.getParameterizedType();
        final boolean optional = isOptionalType(type);
        final Type parameterType = optional ? extractTypeFromOptional(type) : type;
        final Source source = parameter.getAnnotation(Source.class);
        final QName name = (source == null) ? null : resolver.createQName(source.value(), parameter, ownerName);
        return new ListControllerParameter((Class) extractTypeFromList(parameterType), name, optional);
    }

    private ControllerParameter createObjectControllerParameter(Parameter parameter, QName ownerName, QNameResolver resolver) {
        final Type type = parameter.getParameterizedType();
        final boolean optional = isOptionalType(type);
        final Type parameterType = optional ? extractTypeFromOptional(type) : type;
        final Source source = parameter.getAnnotation(Source.class);
        final QName name = (source == null) ? null : resolver.createQName(source.value(), parameter, ownerName);
        return new ObjectControllerParameter((Class) parameterType, name, optional);
    }

    @Override
    public XmlProcessorBuilder<T> addParser(Parser<?> parser) {
        requireNonNull(parser);
        if (parser.getClass().isSynthetic()) {
            throw new IllegalArgumentException("Unfortunately you cannot pass a lambda or method reference to this " +
                    "method. Type information is lost so there's no way to detect the return type. Please use the " +
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
    public XmlProcessorBuilder<T> setXmlInputFactory(XMLInputFactory inputFactory) {
        requireNonNull(inputFactory);
        this.inputFactory = inputFactory;
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
        return new StaxBasedXmlProcessor<>(resultClass, controllerProviders, eventHandlers, parsers, getXmlInputFactory());
    }

    private XMLInputFactory getXmlInputFactory() {
        if (inputFactory != null) {
            return inputFactory;
        }
        final XMLInputFactory factory = XMLInputFactory.newDefaultFactory();
        factory.setProperty(NAMESPACE_AWARE, controllersWithNamespaces > 0);
        factory.setProperty(COALESCE_CHARACTERS, true);
        factory.setProperty(REPLACE_INTERNAL_ENTITY_REFERENCES, true);
        factory.setProperty(SUPPORT_EXTERNAL_ENTITIES, false);
        factory.setProperty(SUPPORT_DTDS, false);
        return factory;
    }
}
