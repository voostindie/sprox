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
import nl.ulso.sprox.ElementNameResolver;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Source;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import static nl.ulso.sprox.impl.ElementReference.createQName;
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

    static ControllerParameter createInjectionParameter(Class<?> controllerClass, Method method, Parameter parameter,
                                                        QName ownerName, NamespaceMap namespaceMap,
                                                        ElementNameResolver resolver) {

        if (parameter.isAnnotationPresent(Attribute.class)) {
            final String attribute = findAnnotation(parameter.getAnnotations(), Attribute.class).value();
            final QName name = createQName(
                    attribute, controllerClass, method, parameter, ownerName, namespaceMap, resolver);
            final Type type = parameter.getParameterizedType();
            return new AttributeControllerParameter(name, resolveObjectClass(type), isOptionalType(type));
        } else if (parameter.isAnnotationPresent(Node.class)) {
            final String node = findAnnotation(parameter.getAnnotations(), Node.class).value();
            final QName name = createQName(
                    node, controllerClass, method, parameter, ownerName, namespaceMap, resolver);
            final Type type = parameter.getParameterizedType();
            return new NodeControllerParameter(ownerName, name, resolveObjectClass(type), isOptionalType(type));
        }
        final Type type = parameter.getParameterizedType();
        final boolean optional = isOptionalType(type);
        final Type parameterType = optional ? extractTypeFromOptional(type) : type;
        final QName sourceName = resolveSourceName(controllerClass, method, parameter, ownerName, namespaceMap, resolver);
        if (isListType(parameterType)) {
            return new ListControllerParameter((Class) extractTypeFromList(parameterType), sourceName, optional);
        } else if (parameterType instanceof Class) {
            return new ObjectControllerParameter((Class) parameterType, sourceName, optional);
        }
        throw new IllegalStateException("Unknown parameter injection type: " + parameterType);
    }

    private static QName resolveSourceName(Class<?> controllerClass, Method method,
                                           Parameter parameter, QName ownerName,
                                           NamespaceMap namespaceMap,
                                           ElementNameResolver resolver) {
        final Source source = findAnnotation(parameter.getAnnotations(), Source.class);
        if (source == null) {
            return null;
        }
        return createQName(source.value(), controllerClass, method, parameter, ownerName, namespaceMap, resolver);
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
}
