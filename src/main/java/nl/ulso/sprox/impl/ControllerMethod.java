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

import nl.ulso.sprox.Node;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import static nl.ulso.sprox.impl.ControllerParameterFactory.createInjectionParameter;

/**
 * Represents a controller method in a controller class.
 */
final class ControllerMethod {
    private final ControllerClass<?> controllerClass;
    private final Method method;
    private final QName ownerName;
    private final int parameterCount;
    private final ControllerParameter[] controllerParameters;

    ControllerMethod(ControllerClass<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.ownerName = resolveOwnerName(method);
        final Parameter[] parameters = method.getParameters();
        parameterCount = parameters.length;
        controllerParameters = new ControllerParameter[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            final Parameter parameter = parameters[i];
            controllerParameters[i] = createInjectionParameter(ownerName, controllerClass, parameter);
        }
    }

    private QName resolveOwnerName(Method method) {
        final String nodeValue = method.getAnnotation(Node.class).value();
        final ElementReference reference = new ElementReference(nodeValue, method.getName());
        return controllerClass.createQName(reference);
    }

    boolean isMatchingStartElement(StartElement node) {
        if (!ownerName.equals(node.getName())) {
            return false;
        }
        for (ControllerParameter controllerParameter : controllerParameters) {
            if (!controllerParameter.isValidStartElement(node)) {
                return false;
            }
        }
        return true;
    }

    boolean isMatchingEndElement(EndElement node) {
        return ownerName.equals(node.getName());
    }

    void processStartElement(StartElement node, ExecutionContext context) {
        for (ControllerParameter controllerParameter : controllerParameters) {
            controllerParameter.pushToExecutionContext(node, context);
        }
    }

    void processEndElement(ExecutionContext context) {
        final Optional<Object[]> methodParameters = constructMethodParameters(context);
        context.removeAttributesAndNodes(ownerName);
        methodParameters
                .ifPresent(parameters -> invokeMethod(context, parameters)
                        .ifPresent(result -> context.pushMethodResult(ownerName, method.getReturnType(), result)));
    }

    private Optional<Object[]> constructMethodParameters(ExecutionContext context) {
        final Object[] methodParameters = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            final Optional parameter = controllerParameters[i].resolveMethodParameter(context);
            if (controllerParameters[i].isOptional()) {
                methodParameters[i] = parameter;
            } else if (parameter.isPresent()) {
                methodParameters[i] = parameter.get();
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(methodParameters);
    }

    private Optional<Object> invokeMethod(ExecutionContext context, Object[] methodParameters) {
        return Optional.ofNullable(controllerClass.invokeMethod(method, context, methodParameters));
    }

    QName getOwnerName() {
        return ownerName;
    }
}
