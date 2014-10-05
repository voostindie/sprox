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

import nl.ulso.sprox.ElementNameResolver;

import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Represents a reference to an XML element.
 * <p>
 * A reference is constructed of two things:
 * </p>
 * <ol>
 * <li>The value of the annotation placed on a method or parameter.</li>
 * <li>The name of the method or parameter.</li>
 * </ol>
 * <p>
 * The annotation value has the following structure, in EBNF: {@code [shorthand ":"] [name]}
 * </p>
 * <p>
 * If no name is specified in the annotation value, the name of the method or parameter is used as the name.
 * </p>
 */
class ElementReference {

    private static final char SEPARATOR = ':';

    private final Optional<String> shorthand;
    private final Optional<String> element;

    private ElementReference(String annotation) {
        final int i = annotation.indexOf(SEPARATOR);
        if (i == -1) {
            shorthand = Optional.empty();
            element = annotation.isEmpty() ? Optional.empty() : Optional.of(annotation);
        } else {
            shorthand = Optional.of(annotation.substring(0, i));
            if (annotation.length() > i + 1) {
                element = Optional.of(annotation.substring(i + 1));
            } else {
                element = Optional.empty();
            }
        }
    }

    static QName createQName(String annotation, Class<?> controllerClass, Method method, NamespaceMap namespaceMap,
                             ElementNameResolver resolver) {
        final ElementReference reference = new ElementReference(annotation);
        final String namespace = reference.shorthand
                .map(namespaceMap::resolveNamespace)
                .orElse(namespaceMap.getDefaultNamespace());
        final String localPart = reference.element.orElse(resolver.fromMethod(controllerClass, method));
        return new QName(namespace, localPart);
    }

    static QName createQName(String annotation, Class<?> controllerClass, Method method, Parameter parameter,
                             QName ownerName, NamespaceMap namespaceMap, ElementNameResolver resolver) {
        final ElementReference reference = new ElementReference(annotation);
        final String namespace = reference.shorthand
                .map(namespaceMap::resolveNamespace)
                .orElse(ownerName.getNamespaceURI());
        final String localPart = reference.element.orElse(resolver.fromParameter(controllerClass, method, parameter));
        return new QName(namespace, localPart);
    }
}
