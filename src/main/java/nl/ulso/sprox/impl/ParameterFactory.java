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

import nl.ulso.sprox.*;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static nl.ulso.sprox.impl.ObjectClasses.resolveObjectClass;

/**
 * Factory for {@link Parameter}s.
 * <p/>
 * The code to create the correct parameter is a bit ugly, thanks to {@code java.lang.reflect}. At least now it's all
 * in one place, nicely isolated.
 */
final class ParameterFactory {
    private ParameterFactory() {
    }

    static Parameter createInjectionParameter(QName owner, Type type, Annotation[] annotations,
                                              String defaultNamespace) {
        final boolean required = findAnnotation(annotations, Nullable.class) == null;
        final Attribute attribute = findAnnotation(annotations, Attribute.class);
        if (attribute != null) {
            final Class parameterClass = resolveObjectClass(type);
            return new AttributeParameter(createQName(attribute.value(), defaultNamespace), parameterClass, required);
        }
        final Node node = findAnnotation(annotations, Node.class);
        if (node != null) {
            final Class parameterClass = resolveObjectClass(type);
            return new NodeParameter(owner, createQName(node.value(), defaultNamespace), parameterClass, required);
        }
        final Source source = findAnnotation(annotations, Source.class);
        final QName sourceNode = source != null ? createQName(source.value(), defaultNamespace) : null;
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType().equals(List.class)) {
                return new ListParameter((Class) parameterizedType.getActualTypeArguments()[0], sourceNode, required);
            }
        } else if (type instanceof Class) {
            return new ObjectParameter((Class) type, sourceNode, required);
        }
        throw new IllegalStateException("Unknown parameter injection type: " + type);
    }

    private static QName createQName(String localPart, String defaultNamespace) {
        if (defaultNamespace == null) {
            return new QName(localPart);
        } else {
            return new QName(defaultNamespace, localPart);
        }
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
