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

import javax.xml.stream.events.XMLEvent;
import java.lang.reflect.Method;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Event handler for start nodes. For every controller there's exactly one such event handler. At the start of the
 * execution of an {@link nl.ulso.sprox.impl.StaxBasedXmlProcessor} it has a list of {@code StartNodeEventHandler}s
 * only.
 */
final class StartNodeEventHandler implements EventHandler {
    private final ControllerMethod controllerMethod;
    private final EventHandler nodeEventHandler;

    StartNodeEventHandler(ControllerClass<?> controllerClass, Method method, boolean recursive) {
        this.controllerMethod = new ControllerMethod(controllerClass, method);
        if (recursive) {
            this.nodeEventHandler = new RecursiveNodeEventHandler(this, controllerMethod);
        } else {
            this.nodeEventHandler = new NonRecursiveNodeEventHandler(this, controllerMethod);
        }
    }

    @Override
    public boolean matches(XMLEvent event, ExecutionContext context) {
        switch (event.getEventType()) {
            case START_ELEMENT:
                return controllerMethod.isMatchingStartElement(event.asStartElement());
            default:
                return false;
        }
    }

    @Override
    public EventHandler process(XMLEvent event, ExecutionContext context) {
        controllerMethod.processStartElement(event.asStartElement(), context);
        if (nodeEventHandler.matches(event, context)) {
            return nodeEventHandler.process(event, context);
        } else {
            return nodeEventHandler;
        }
    }
}
