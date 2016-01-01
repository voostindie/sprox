package nl.ulso.sprox.resolvers;

import nl.ulso.sprox.ElementNameResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * {@link nl.ulso.sprox.ElementNameResolver} that translates parameter and method names to all lower case names, with
 * hyphens to separate the words.
 */
public class CamelCaseToHyphensElementNameResolver implements ElementNameResolver {
    @Override
    public String fromParameter(Class<?> controllerClass, Method method, Parameter parameter) {
        return translate(parameter.getName());
    }

    @Override
    public String fromMethod(Class<?> controllerClass, Method method) {
        return translate(method.getName());
    }

    private String translate(String name) {
        StringBuilder builder = new StringBuilder(name.length());
        final char[] chars = name.toCharArray();
        final int size = chars.length;
        for (int i = 0; i < size; i++) {
            if (Character.isUpperCase(chars[i])) {
                if (i > 0) {
                    builder.append("-");
                }
                builder.append(Character.toLowerCase(chars[i]));
            } else {
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }
}
