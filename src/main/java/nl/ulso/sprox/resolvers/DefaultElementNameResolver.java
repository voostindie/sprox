package nl.ulso.sprox.resolvers;

import nl.ulso.sprox.ElementNameResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Default {@link nl.ulso.sprox.ElementNameResolver}. It returns method and parameter names as is.
 */
public class DefaultElementNameResolver implements ElementNameResolver {
    @Override
    public String fromParameter(Class<?> controllerClass, Method method, Parameter parameter) {
        return parameter.getName();
    }

    @Override
    public String fromMethod(Class<?> controllerClass, Method method) {
        return method.getName();
    }
}
