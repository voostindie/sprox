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

import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessorException;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static nl.ulso.sprox.impl.ParameterFactory.createInjectionParameter;

/**
 * Represents a controller method in a controller class.
 */
final class ControllerMethod {
    private final ControllerClass<?> controllerClass;
    private final Method method;
    private final QName owner;
    private final int parameterCount;
    private final Parameter[] parameters;

    ControllerMethod(ControllerClass<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.owner = controllerClass.createQName(method.getAnnotation(Node.class).value());
        final Type[] parameterTypes = method.getGenericParameterTypes();
        parameterCount = parameterTypes.length;
        parameters = new Parameter[parameterCount];
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterCount; i++) {
            parameters[i] = createInjectionParameter(owner, controllerClass, parameterTypes[i], parameterAnnotations[i]);
        }
    }

    boolean isMatchingStartElement(StartElement node) {
        if (!owner.equals(node.getName())) {
            return false;
        }
        for (Parameter parameter : parameters) {
            if (!parameter.isValidStartElement(node)) {
                return false;
            }
        }
        return true;
    }

    boolean isMatchingEndElement(EndElement node) {
        return owner.equals(node.getName());
    }

    /**
     * Inspects the node and registers all elements that must be kept track of in the execution context. Only
     * called if {@link #isMatchingStartElement(javax.xml.stream.events.StartElement)} returns {@code true}.
     */
    void processStartElement(StartElement node, ExecutionContext context) {
        for (Parameter parameter : parameters) {
            parameter.pushToExecutionContext(node, context);
        }
    }

    /**
     * Invokes the method in this context, passing it all the parameters collected in the context.
     *
     * @param context Context containing all collected data; may not be {@code null}
     */
    void processEndElement(ExecutionContext context) throws XmlProcessorException {
        final Object[] methodParameters = constructMethodParameters(context);
        context.removeAttributesAndNodes(owner);
        if (verifyMethodParameters(methodParameters)) {
            Object result = invokeMethod(context, methodParameters);
            if (result != null) {
                context.pushMethodResult(owner, method.getReturnType(), result);
            }
        }
    }

    private Object[] constructMethodParameters(ExecutionContext context) throws XmlProcessorException {
        final Object[] methodParameters = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            methodParameters[i] = parameters[i].resolveMethodParameter(context);
        }
        return methodParameters;
    }

    private boolean verifyMethodParameters(Object[] methodParameters) {
        for (int i = 0; i < parameterCount; i++) {
            if (methodParameters[i] == null && parameters[i].isRequired()) {
                return false;
            }
        }
        return true;
    }

    private Object invokeMethod(ExecutionContext context, Object[] methodParameters) throws XmlProcessorException {
        return controllerClass.invokeMethod(method, context, methodParameters);
    }

    QName getOwner() {
        return owner;
    }
}
