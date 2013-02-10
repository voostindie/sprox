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

import nl.ulso.sprox.Namespace;
import nl.ulso.sprox.Namespaces;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessorException;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static nl.ulso.sprox.impl.ParameterFactory.createInjectionParameter;

/**
 * Represents a controller method in a controller class.
 */
final class ControllerMethod {
    private final Class<?> controllerClass;
    private final Method method;
    private final QName owner;
    private final int parameterCount;
    private final Parameter[] parameters;

    ControllerMethod(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
        final Map<String, String> namespaces = extractNamespaces(controllerClass);
        final String defaultNamespace = determineDefaultNamespace(controllerClass, method, namespaces);
        this.owner = new QName(defaultNamespace, method.getAnnotation(Node.class).value());
        final Type[] parameterTypes = method.getGenericParameterTypes();
        parameterCount = parameterTypes.length;
        parameters = new Parameter[parameterCount];
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterCount; i++) {
            parameters[i] = createInjectionParameter(owner, namespaces, defaultNamespace,
                    parameterTypes[i], parameterAnnotations[i]);
        }
    }

    private Map<String, String> extractNamespaces(Class<?> controllerClass) {
        final Namespaces namespaces = controllerClass.getAnnotation(Namespaces.class);
        final Namespace namespace = controllerClass.getAnnotation(Namespace.class);
        if (namespaces != null && namespace != null) {
            throw new IllegalStateException("Controller class '" + controllerClass
                    + "' must have either no annotations, a @Namespace or a @Namespaces annotation. This one has both.");
        }
        if (namespaces != null) {
            return extractMultipleNamespaces(namespaces);
        } else if (namespace != null) {
            return extractSingleNamespace(namespace);
        }
        return emptyMap();
    }

    private Map<String, String> extractMultipleNamespaces(Namespaces namespaces) {
        Map<String, String> result = new HashMap<>(namespaces.value().length);
        for (Namespace namespace : namespaces.value()) {
            final String shorthand = namespace.shorthand();
            final String name = namespace.value();
            if (shorthand.isEmpty()) {
                throw new IllegalStateException("Namespace in a list of namespace MUST have a shorthand. "
                        + name);
            }
            if (result.containsKey(shorthand)) {
                throw new IllegalStateException("Duplicate shorthands are not allowed: " + shorthand);
            }
            result.put(shorthand, name);
        }
        return result;
    }

    private Map<String, String> extractSingleNamespace(Namespace namespace) {
        Map<String, String> result = new HashMap<>(1);
        result.put(namespace.shorthand(), namespace.value());
        return result;
    }

    private String determineDefaultNamespace(Class<?> controllerClass, Method method, Map<String, String> namespaces) {
        final String namespace = method.getAnnotation(Node.class).ns();
        if (!namespace.isEmpty()) {
            if (!namespaces.containsKey(namespace)) {
                throw new IllegalStateException("Unknown namespace '" + namespace + "' defined for @Node on method '"
                        + method + "' of class '" + controllerClass + "'");
            }
            return namespaces.get(namespace);
        }
        if (namespaces.isEmpty()) {
            return null;
        }
        if (controllerClass.isAnnotationPresent(Namespace.class)) {
            return namespaces.values().iterator().next();
        }
        final String defaultShorthand = controllerClass.getAnnotation(Namespaces.class).defaultShorthand();
        if (!namespaces.containsKey(defaultShorthand)) {
            throw new IllegalStateException("Invalid default namespace '" + defaultShorthand + "' for class '"
                    + controllerClass + "'");
        }
        return namespaces.get(defaultShorthand);
    }

    boolean isMatchingStartElement(StartElement node) {
        if (!owner.equals(node.getName())) {
            return false;
        }
        for (Parameter parameter : parameters) {
            if (!parameter.isValidStartElement(node)) {
                return false;
            }
        }
        return true;
    }

    boolean isMatchingEndElement(EndElement node) {
        return owner.equals(node.getName());
    }

    /**
     * Inspects the node and registers all elements that must be kept track of in the execution context. Only
     * called if {@link #isMatchingStartElement(javax.xml.stream.events.StartElement)} returns {@code true}.
     */
    void processStartElement(StartElement node, ExecutionContext context) {
        for (Parameter parameter : parameters) {
            parameter.pushToExecutionContext(node, context);
        }
    }

    /**
     * Invokes the method in this context, passing it all the parameters collected in the context.
     *
     * @param context Context containing all collected data; may not be {@code null}
     */
    void processEndElement(ExecutionContext context) throws XmlProcessorException {
        final Object[] methodParameters = constructMethodParameters(context);
        context.removeAttributesAndNodes(owner);
        if (verifyMethodParameters(methodParameters)) {
            Object result = invokeMethod(context, methodParameters);
            if (result != null) {
                context.pushMethodResult(owner, method.getReturnType(), result);
            }
        }
    }

    private Object[] constructMethodParameters(ExecutionContext context) throws XmlProcessorException {
        final Object[] methodParameters = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            methodParameters[i] = parameters[i].resolveMethodParameter(context);
        }
        return methodParameters;
    }

    private boolean verifyMethodParameters(Object[] methodParameters) {
        for (int i = 0; i < parameterCount; i++) {
            if (methodParameters[i] == null && parameters[i].isRequired()) {
                return false;
            }
        }
        return true;
    }

    private Object invokeMethod(ExecutionContext context, Object[] methodParameters) {
        try {
            return method.invoke(context.getController(controllerClass), methodParameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    QName getOwner() {
        return owner;
    }
}
