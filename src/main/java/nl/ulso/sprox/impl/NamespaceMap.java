package nl.ulso.sprox.impl;

import nl.ulso.sprox.Namespace;
import nl.ulso.sprox.Namespaces;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static javax.xml.XMLConstants.NULL_NS_URI;

/**
 * Represents the set of namespaces on a single controller class.
 */
final class NamespaceMap {
    private final Map<String, String> namespaces;
    private final String defaultNamespace;

    NamespaceMap(Class<?> controllerClass) {
        this.namespaces = extractNamespaces(controllerClass);
        this.defaultNamespace = determineDefaultNamespace(controllerClass, namespaces);
    }

    private static <T> Map<String, String> extractNamespaces(Class<T> controllerClass) {
        final Namespaces namespacesAnnotation = controllerClass.getAnnotation(Namespaces.class);
        final Namespace namespaceAnnotation = controllerClass.getAnnotation(Namespace.class);
        if (namespacesAnnotation != null && namespaceAnnotation != null) {
            throw new IllegalStateException("Controller class '" + controllerClass
                    + "' must have either no annotations, a @Namespace annotation, or a @Namespaces annotation. "
                    + "This one has both.");
        }
        if (namespacesAnnotation == null && namespaceAnnotation == null) {
            return emptyMap();
        }
        return createStream(namespacesAnnotation, namespaceAnnotation).collect(
                toMap(Namespace::shorthand, Namespace::value));
    }

    private static Stream<Namespace> createStream(Namespaces namespacesAnnotation, Namespace namespaceAnnotation) {
        if (namespacesAnnotation != null) {
            return Arrays.stream(namespacesAnnotation.value());
        } else {
            return Stream.of(namespaceAnnotation);
        }
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

    boolean hasNamespacesDeclared() {
        return !namespaces.isEmpty();
    }

    String getDefaultNamespace() {
        return defaultNamespace;
    }

    String resolveNamespace(String shorthand) {
        return namespaces.getOrDefault(shorthand, NULL_NS_URI);
    }
}
