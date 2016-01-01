package nl.ulso.sprox;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Resolves the name of an XML element from a Java method or parameter.
 * <p>
 * The {@link nl.ulso.sprox.XmlProcessorBuilder} always uses exactly one {@code ElementNameResolver} at a time while
 * processing controllers. It uses it when a controller method or parameter is annotated with
 * {@link nl.ulso.sprox.Node}, {@link nl.ulso.sprox.Attribute}, or {@link nl.ulso.sprox.Source} and no element name is
 * provided in the annotation itself. The builder then uses the resolver to determine the element name.
 * </p>
 * <p>
 * The resolver in use by the builder can be changed by calling the
 * {@link nl.ulso.sprox.XmlProcessorBuilder#setElementNameResolver(ElementNameResolver)} and
 * {@link XmlProcessorBuilder#resetElementNameResolver()}) methods. This allows you to plug in your own implementation
 * for one or more controllers.
 * </p>
 * <p>
 * You don't need to set a resolver explicitly. The {@link nl.ulso.sprox.XmlProcessorBuilder} uses the
 * {@link nl.ulso.sprox.resolvers.DefaultElementNameResolver} if none is specified.
 * </p>
 * <p>
 * A typical use case is the {@link nl.ulso.sprox.resolvers.CamelCaseToHyphensElementNameResolver}.
 * </p>
 */
public interface ElementNameResolver {

    String fromParameter(Class<?> controllerClass, Method method, Parameter parameter);

    String fromMethod(Class<?> controllerClass, Method method);
}
