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

import nl.ulso.sprox.XmlProcessorException;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;
import static nl.ulso.sprox.impl.UncheckedXmlProcessorException.unchecked;

/**
 * Represents a controller method in a controller class.
 */
final class ControllerMethod {
    private final Class<?> controllerClass;
    private final Method method;
    private final QName ownerName;
    private final int parameterCount;
    private final List<ControllerParameter> controllerParameters;

    ControllerMethod(Class<?> controllerClass, Method method, QName ownerName,
                     List<ControllerParameter> controllerParameters) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.ownerName = ownerName;
        this.parameterCount = controllerParameters.size();
        this.controllerParameters = unmodifiableList(controllerParameters);
    }


    boolean isMatchingStartElement(StartElement node) {
        return ownerName.equals(node.getName())
                && controllerParameters.stream().allMatch(parameter -> parameter.isValidStartElement(node));
    }

    boolean isMatchingEndElement(EndElement node) {
        return ownerName.equals(node.getName());
    }

    void processStartElement(StartElement node, ExecutionContext context) {
        controllerParameters.stream().forEach(p -> p.pushToExecutionContext(node, context));
    }

    void processEndElement(ExecutionContext context) {
        final Object[] parameters = constructMethodParameters(context);
        context.removeAttributesAndNodes(ownerName);
        if (parameters.length == parameterCount) {
            invokeMethod(context, parameters)
                    .ifPresent(result -> context.pushMethodResult(ownerName, method.getReturnType(), result));
        }
    }

    private Object[] constructMethodParameters(ExecutionContext context) {
        return controllerParameters.stream()
                .map(controllerParameter -> {
                    final Optional parameter = controllerParameter.resolveMethodParameter(context);
                    if (controllerParameter.isOptional()) {
                        //noinspection unchecked
                        return parameter;
                    } else if (parameter.isPresent()) {
                        return parameter.get();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray();
    }

    private Optional<Object> invokeMethod(ExecutionContext context, Object[] methodParameters) {
        try {
            return Optional.ofNullable(method.invoke(context.getController(controllerClass), methodParameters));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Access to controller method '" + method + "' was denied.", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw unchecked(new XmlProcessorException("Invocation of controller method '" + method
                    + "' resulted in an exception.", e.getCause()));
        }
    }

    QName getOwnerName() {
        return ownerName;
    }
}
