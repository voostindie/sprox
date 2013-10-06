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

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Nullable;
import nl.ulso.sprox.Source;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static nl.ulso.sprox.impl.ReflectionUtil.resolveObjectClass;

/**
 * Factory for {@link ControllerParameter}s.
 * <p/>
 * The code to create the correct parameter is a bit ugly, thanks to {@code java.lang.reflect}. At least now it's all
 * in one place, nicely isolated.
 */
final class ControllerParameterFactory {
    private ControllerParameterFactory() {
    }

    static ControllerParameter createInjectionParameter(QName owner, ControllerClass<?> controllerClass, Type type,
                                              Annotation[] annotations) {
        final boolean required = findAnnotation(annotations, Nullable.class) == null;
        final Attribute attribute = findAnnotation(annotations, Attribute.class);
        if (attribute != null) {
            final Class parameterClass = resolveObjectClass(type);
            final QName name = controllerClass.createQName(attribute.value(), owner.getNamespaceURI());
            return new AttributeControllerParameter(name, parameterClass, required);
        }
        final Node node = findAnnotation(annotations, Node.class);
        if (node != null) {
            final Class parameterClass = resolveObjectClass(type);
            final QName name = controllerClass.createQName(node.value(), owner.getNamespaceURI());
            return new NodeControllerParameter(owner, name, parameterClass, required);
        }
        final Source source = findAnnotation(annotations, Source.class);
        final QName sourceName = source == null ? null : controllerClass.createQName(
                source.value(), owner.getNamespaceURI());
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType().equals(List.class)) {
                return new ListControllerParameter(
                        (Class) parameterizedType.getActualTypeArguments()[0], sourceName, required);
            }
        } else if (type instanceof Class) {
            return new ObjectControllerParameter((Class) type, sourceName, required);
        }
        throw new IllegalStateException("Unknown parameter injection type: " + type);
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
