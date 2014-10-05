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
