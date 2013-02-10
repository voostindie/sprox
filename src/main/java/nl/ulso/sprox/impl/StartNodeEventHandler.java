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

final class StartNodeEventHandler implements EventHandler {
    private final ControllerMethod controllerMethod;
    private EventHandler insideNodeEventHandler;


    static EventHandler createStartNodeEventHandler(Class controllerClass, Method method) {
        StartNodeEventHandler eventHandler = new StartNodeEventHandler(controllerClass, method);
        eventHandler.initialize();
        return eventHandler;
    }

    private StartNodeEventHandler(Class controllerClass, Method method) {
        this.controllerMethod = new ControllerMethod(controllerClass, method);
    }

    // A separate initialization step is necessary because otherwise "this" would escape the constructor.
    private void initialize() {
        this.insideNodeEventHandler = new InsideNodeEventHandler(this, controllerMethod);
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
        switch (event.getEventType()) {
            case START_ELEMENT:
                controllerMethod.processStartElement(event.asStartElement(), context);
                if (insideNodeEventHandler.matches(event, context)) {
                    return insideNodeEventHandler.process(event, context);
                } else {
                    return insideNodeEventHandler;
                }
            default:
                throw new IllegalStateException("Unsupported event type: " + event.getEventType());
        }
    }
}