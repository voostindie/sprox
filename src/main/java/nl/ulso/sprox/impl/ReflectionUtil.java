package nl.ulso.sprox.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides utility methods for reflection.
 */
final class ReflectionUtil {
    private static final Map<Type, Class> PRIMITIVE_PARAMETER_TYPES = Map.of(
            Boolean.class, Boolean.TYPE,
            Byte.class, Byte.TYPE,
            Character.class, Character.TYPE,
            Double.class, Double.TYPE,
            Float.class, Float.TYPE,
            Integer.class, Integer.TYPE,
            Long.class, Long.TYPE,
            Short.class, Short.TYPE);

    private ReflectionUtil() {
    }

    /**
     * Resolves the {@link Class} that corresponds with a {@link Type}.
     * <p>
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
        if (isOptionalType(objectType)) {
            return resolveObjectClass(extractTypeFromOptional(objectType));
        }
        try {
            return (Class) objectType;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Cannot resolve object class from type: " + objectType, e);
        }
    }

    static boolean isOptionalType(Type objectType) {
        if (objectType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) objectType;
            return parameterizedType.getRawType().equals(Optional.class);
        }
        return false;
    }

    static Type extractTypeFromOptional(Type optionalType) {
        return ((ParameterizedType) optionalType).getActualTypeArguments()[0];
    }

    static boolean isListType(Type objectType) {
        if (objectType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) objectType;
            return parameterizedType.getRawType().equals(List.class);
        }
        return false;
    }

    static Type extractTypeFromList(Type listType) {
        return ((ParameterizedType) listType).getActualTypeArguments()[0];
    }
}
