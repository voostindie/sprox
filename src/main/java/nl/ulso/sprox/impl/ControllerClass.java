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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Represents a controller class.
 * <p/>
 * On construction all namespaces (if any) are pulled from from the class and verified. All {@link ControllerMethod}s
 * for the same class share the same instance of this class, providing access to the namespaces.
 */
final class ControllerClass<T> {

    private final Class<T> clazz;
    private final Map<String, String> namespaces;
    private String defaultNamespace;

    static <T> ControllerClass<T> createControllerClass(Class<T> clazz) {
        final ControllerClass<T> controllerClass = new ControllerClass<>(clazz);
        controllerClass.initialize();
        return controllerClass;
    }

    private ControllerClass(Class<T> clazz) {
        this.clazz = clazz;
        this.namespaces = extractNamespaces(clazz);
    }

    // A separate initialization step is necessary because otherwise "this" would escape the constructor.
    private void initialize() {
        defaultNamespace = determineDefaultNamespace();
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

    private String determineDefaultNamespace() {
        if (namespaces.isEmpty()) {
            return null;
        }
        if (clazz.isAnnotationPresent(Namespace.class)) {
            return clazz.getAnnotation(Namespace.class).value();
        }
        return clazz.getAnnotation(Namespaces.class).value()[0].value();
    }

    String determineDefaultMethodNamespace(Method method) {
        final String namespace = method.getAnnotation(Node.class).ns();
        if (!namespace.isEmpty()) {
            if (!namespaces.containsKey(namespace)) {
                throw new IllegalStateException("Unknown namespace '" + namespace + "' defined for @Node on method '"
                        + method + "' of class '" + clazz + "'");
            }
            return namespaces.get(namespace);
        }
        return defaultNamespace;
    }


    String getNamespace(String shorthand) {
        final String namespace = namespaces.get(shorthand);
        if (namespace == null) {
            throw new IllegalStateException("Invalid namespace shorthand '" + shorthand + "'");
        }
        return namespace;
    }

    Object invokeMethod(Method method, ExecutionContext context, Object[] methodParameters) {
        try {
            return method.invoke(context.getController(clazz), methodParameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not invoke method '" + method
                    + "' on controller of class '" + clazz + "'");
        }
    }
}
