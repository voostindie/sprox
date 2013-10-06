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
        this.ownerName = controllerClass.createQName(method.getAnnotation(Node.class).value());
        final Type[] parameterTypes = method.getGenericParameterTypes();
        parameterCount = parameterTypes.length;
        controllerParameters = new ControllerParameter[parameterCount];
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterCount; i++) {
            controllerParameters[i] = createInjectionParameter(
                    ownerName, controllerClass, parameterTypes[i], parameterAnnotations[i]);
        }
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

    void processEndElement(ExecutionContext context) throws XmlProcessorException {
        final Object[] methodParameters = constructMethodParameters(context);
        context.removeAttributesAndNodes(ownerName);
        if (verifyMethodParameters(methodParameters)) {
            Object result = invokeMethod(context, methodParameters);
            if (result != null) {
                context.pushMethodResult(ownerName, method.getReturnType(), result);
            }
        }
    }

    private Object[] constructMethodParameters(ExecutionContext context) throws XmlProcessorException {
        final Object[] methodParameters = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            methodParameters[i] = controllerParameters[i].resolveMethodParameter(context);
        }
        return methodParameters;
    }

    private boolean verifyMethodParameters(Object[] methodParameters) {
        for (int i = 0; i < parameterCount; i++) {
            if (methodParameters[i] == null && controllerParameters[i].isRequired()) {
                return false;
            }
        }
        return true;
    }

    private Object invokeMethod(ExecutionContext context, Object[] methodParameters) throws XmlProcessorException {
        return controllerClass.invokeMethod(method, context, methodParameters);
    }

    QName getOwnerName() {
        return ownerName;
    }
}
