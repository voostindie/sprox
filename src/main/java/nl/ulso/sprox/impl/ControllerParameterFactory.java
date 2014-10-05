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

    static ControllerParameter createInjectionParameter(Parameter parameter, QName ownerName, QNameResolver resolver) {
        if (parameter.isAnnotationPresent(Attribute.class)) {
            final String attribute = parameter.getAnnotation(Attribute.class).value();
            final QName name = resolver.createQName(attribute, parameter, ownerName);
            final Type type = parameter.getParameterizedType();
            return new AttributeControllerParameter(name, resolveObjectClass(type), isOptionalType(type));

        } else if (parameter.isAnnotationPresent(Node.class)) {
            final String node = parameter.getAnnotation(Node.class).value();
            final QName name = resolver.createQName(node, parameter, ownerName);
            final Type type = parameter.getParameterizedType();
            return new NodeControllerParameter(ownerName, name, resolveObjectClass(type), isOptionalType(type));
        }

        final Type type = parameter.getParameterizedType();
        final boolean optional = isOptionalType(type);
        final Type parameterType = optional ? extractTypeFromOptional(type) : type;
        final Source source = parameter.getAnnotation(Source.class);
        final QName name;
        if (source == null) {
            name = null;
        } else {
            name = resolver.createQName(source.value(), parameter, ownerName);
        }

        if (isListType(parameterType)) {
            return new ListControllerParameter((Class) extractTypeFromList(parameterType), name, optional);

        } else if (parameterType instanceof Class) {
            return new ObjectControllerParameter((Class) parameterType, name, optional);
        }
        throw new IllegalStateException("Unknown parameter injection type: " + parameterType);
    }
}
