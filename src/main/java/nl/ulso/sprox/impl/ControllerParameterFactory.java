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

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Source;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import static nl.ulso.sprox.impl.ReflectionUtil.*;

/**
 * Factory for {@link ControllerParameter}s.
 * <p>
 * The code to create the correct parameter is a bit ugly, thanks to {@code java.lang.reflect}. At least now it's all
 * in one place, nicely isolated.
 */
final class ControllerParameterFactory {
    private ControllerParameterFactory() {
    }

    static ControllerParameter createInjectionParameter(QName owner, NamespaceMap namespaceMap, Parameter parameter) {

        if (parameter.isAnnotationPresent(Attribute.class)) {
            return createAttributeControllerParameter(owner, namespaceMap, parameter);
        } else if (parameter.isAnnotationPresent(Node.class)) {
            return createNodeControllerParameter(owner, namespaceMap, parameter);
        }
        final Type type = parameter.getParameterizedType();
        final Type parameterType = isOptionalType(type) ? extractTypeFromOptional(type) : type;
        if (isListType(parameterType)) {
            return createListControllerParameter(owner, namespaceMap, parameter);
        } else if (parameterType instanceof Class) {
            return createObjectControllerParameter(owner, namespaceMap, parameter);
        }
        throw new IllegalStateException("Unknown parameter injection type: " + parameterType);
    }

    private static ControllerParameter createAttributeControllerParameter(QName owner, NamespaceMap namespaceMap,
                                                                          Parameter parameter) {
        final Type type = parameter.getParameterizedType();
        final Attribute attribute = findAnnotation(parameter.getAnnotations(), Attribute.class);
        final QName name = new ElementReference(attribute.value(), parameter.getName(), namespaceMap, owner.getNamespaceURI()).asQName();
        return new AttributeControllerParameter(name, resolveObjectClass(type), isOptionalType(type));
    }

    private static ControllerParameter createNodeControllerParameter(QName owner, NamespaceMap namespaceMap,
                                                                     Parameter parameter) {
        final Type type = parameter.getParameterizedType();
        final Node node = findAnnotation(parameter.getAnnotations(), Node.class);
        final QName name = new ElementReference(node.value(), parameter.getName(), namespaceMap, owner.getNamespaceURI()).asQName();
        return new NodeControllerParameter(owner, name, resolveObjectClass(type), isOptionalType(type));
    }

    private static ControllerParameter createListControllerParameter(QName owner, NamespaceMap namespaceMap, Parameter parameter) {
        final Type type = parameter.getParameterizedType();
        final QName sourceName = resolveSourceName(owner, namespaceMap, parameter);
        final boolean optional = isOptionalType(type);
        final Type parameterType = optional ? extractTypeFromOptional(type) : type;
        return new ListControllerParameter((Class) extractTypeFromList(parameterType), sourceName, optional);
    }

    private static ControllerParameter createObjectControllerParameter(QName owner, NamespaceMap namespaceMap, Parameter parameter) {
        final Type type = parameter.getParameterizedType();
        final QName sourceName = resolveSourceName(owner, namespaceMap, parameter);
        final boolean optional = isOptionalType(type);
        final Type parameterType = optional ? extractTypeFromOptional(type) : type;
        return new ObjectControllerParameter((Class) parameterType, sourceName, optional);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A findAnnotation(Annotation[] annotations, Class<A> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotationClass.isInstance(annotation)) {
                return (A) annotation;
            }
        }
        return null;
    }

    private static QName resolveSourceName(QName owner, NamespaceMap namespaceMap, Parameter parameter) {
        final Source source = findAnnotation(parameter.getAnnotations(), Source.class);
        if (source == null) {
            return null;
        }
        return new ElementReference(source.value(), parameter.getName(), namespaceMap, owner.getNamespaceURI()).asQName();
    }
}
