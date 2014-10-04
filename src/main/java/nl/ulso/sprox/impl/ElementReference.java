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

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Optional;

import static javax.xml.XMLConstants.NULL_NS_URI;

/**
 * Represents a reference to an XML element.
 * <p>
 * A reference is constructed of two things:
 * </p>
 * <ol>
 * <li>The value of the annotation placed on a method or parameter</li>
 * <li>The name of the method or parameter</li>
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
    private final String element;

    ElementReference(String annotationValue, String annotatedElementName) {
        final int i = annotationValue.indexOf(SEPARATOR);
        if (i == -1) {
            shorthand = Optional.empty();
            element = resolveElement(annotationValue, annotatedElementName);
        } else {
            shorthand = Optional.of(annotationValue.substring(0, i));
            element = resolveElement(annotationValue.substring(i + 1), annotatedElementName);
        }
    }

    private String resolveElement(String annotationValue, String annotatedElementName) {
        return annotationValue.isEmpty() ? annotatedElementName : annotationValue;
    }

    QName createQName(Map<String, String> namespaces, String defaultNamespace) {
        return new QName(
                shorthand.map(key -> namespaces.getOrDefault(key, NULL_NS_URI)).orElse(defaultNamespace),
                element
        );
    }
}
