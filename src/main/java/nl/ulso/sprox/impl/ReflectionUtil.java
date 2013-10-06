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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides utility methods for reflection.
 */
final class ReflectionUtil {
    private static final Map<Type, Class> PRIMITIVE_PARAMETER_TYPES;

    static {
        PRIMITIVE_PARAMETER_TYPES = new HashMap<>(8, 1.0f);
        PRIMITIVE_PARAMETER_TYPES.put(Boolean.class, Boolean.TYPE);
        PRIMITIVE_PARAMETER_TYPES.put(Byte.class, Byte.TYPE);
        PRIMITIVE_PARAMETER_TYPES.put(Character.class, Character.TYPE);
        PRIMITIVE_PARAMETER_TYPES.put(Double.class, Double.TYPE);
        PRIMITIVE_PARAMETER_TYPES.put(Float.class, Float.TYPE);
        PRIMITIVE_PARAMETER_TYPES.put(Integer.class, Integer.TYPE);
        PRIMITIVE_PARAMETER_TYPES.put(Long.class, Long.TYPE);
        PRIMITIVE_PARAMETER_TYPES.put(Short.class, Short.TYPE);
    }

    private ReflectionUtil() {
    }

    /**
     * Resolves the {@link Class} that corresponds with a {@link Type}.
     * <p/>
     * {@code Class} is an implementation of {@code Type} but, of course, not every {@code Type} is a {@code Class}.
     * Case in point here are primitive types. These can appear as method parameters.
     *
     * @param objectType Type to resolve.
     * @return Corresponding class.
     */
    static Class resolveObjectClass(Type objectType) {
        final Class type = PRIMITIVE_PARAMETER_TYPES.get(objectType);
        if (type != null) {
            return type;
        }
        try {
            return (Class) objectType;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Cannot resolve object class from type: " + objectType, e);
        }
    }
}