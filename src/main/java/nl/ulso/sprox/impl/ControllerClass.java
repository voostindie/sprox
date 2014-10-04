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

import nl.ulso.sprox.Namespace;
import nl.ulso.sprox.Namespaces;
import nl.ulso.sprox.XmlProcessorException;

import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static nl.ulso.sprox.impl.UncheckedXmlProcessorException.unchecked;

/**
 * Represents a controller class.
 * <p>
 * On construction all namespaces (if any) are pulled from from the class and verified. All {@link ControllerMethod}s
 * for the same class share the same instance of this class, providing access to the namespaces.
 */
final class ControllerClass<T> {

    private final Class<T> clazz;
    private final Map<String, String> namespaces;
    private final String defaultNamespace;

    ControllerClass(Class<T> clazz) {
        this.clazz = clazz;
        this.namespaces = unmodifiableMap(extractNamespaces(clazz));
        this.defaultNamespace = determineDefaultNamespace(clazz, namespaces);
    }

    private static <T> Map<String, String> extractNamespaces(Class<T> clazz) {
        final Namespaces namespacesAnnotation = clazz.getAnnotation(Namespaces.class);
        final Namespace namespaceAnnotation = clazz.getAnnotation(Namespace.class);
        if (namespacesAnnotation != null && namespaceAnnotation != null) {
            throw new IllegalStateException("Controller class '" + clazz
                    + "' must have either no annotations, a @Namespace or a @Namespaces annotation. "
                    + "This one has both.");
        }
        if (namespacesAnnotation != null) {
            return extractMultipleNamespaces(namespacesAnnotation);
        } else if (namespaceAnnotation != null) {
            return extractSingleNamespace(namespaceAnnotation);
        }
        return emptyMap();
    }

    private static Map<String, String> extractMultipleNamespaces(Namespaces namespacesAnnotation) {
        Map<String, String> result = new HashMap<>(namespacesAnnotation.value().length);
        for (Namespace namespace : namespacesAnnotation.value()) {
            final String shorthand = namespace.shorthand();
            final String name = namespace.value();
            if (result.containsKey(shorthand)) {
                throw new IllegalStateException("Duplicate shorthands are not allowed: " + shorthand);
            }
            result.put(shorthand, name);
        }
        return result;
    }

    private static Map<String, String> extractSingleNamespace(Namespace namespaceAnnotation) {
        Map<String, String> result = new HashMap<>(1);
        result.put(namespaceAnnotation.shorthand(), namespaceAnnotation.value());
        return result;
    }

    private static <T> String determineDefaultNamespace(Class<T> clazz, Map<String, String> namespaces) {
        if (namespaces.isEmpty()) {
            return null;
        }
        if (clazz.isAnnotationPresent(Namespace.class)) {
            return clazz.getAnnotation(Namespace.class).value();
        }
        return clazz.getAnnotation(Namespaces.class).value()[0].value();
    }

    /**
     * Invokes the specified method on the controller of this class in the execution context, passing it the method
     * parameters.
     *
     * @param method           Method to invoke.
     * @param context          Context to pull the controller for this class from.
     * @param methodParameters Parameters to pass to the controller.
     * @return The result of invoking the method.
     * @throws IllegalStateException If the method could not be invoked.
     */
    Object invokeMethod(Method method, ExecutionContext context, Object[] methodParameters) {
        try {
            return method.invoke(context.getController(clazz), methodParameters);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Access to controller method '" + method + "' was denied.", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw unchecked(new XmlProcessorException("Invocation of controller method '" + method
                    + "' resulted in an exception.", e.getCause()));
        }
    }

    /**
     * Constructs a QName from an element reference using the default namespace as set on the class (if any) if none
     * was defined in the reference itself.
     *
     * @param reference Value to construct the QName for.
     * @return A new QName
     */
    QName createQName(ElementReference reference) {
        return createQName(reference, defaultNamespace);
    }

    /**
     * Constructs a QName from an element reference using the default namespace as set on the element (if any) if none
     * was defined in the reference itself.
     *
     * @param reference               Value to construct the QName for.
     * @param defaultElementNamespace Namespace to use if the reference defines none.
     * @return A new QName
     */
    QName createQName(ElementReference reference, String defaultElementNamespace) {
        return reference.createQName(namespaces, defaultElementNamespace);
    }
}
