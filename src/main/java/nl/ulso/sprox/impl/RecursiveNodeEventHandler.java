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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Event handler for recursive nodes.
 */
final class RecursiveNodeEventHandler implements EventHandler {
    private final EventHandler parentEventHandler;
    private final ControllerMethod controllerMethod;
    private int level;

    RecursiveNodeEventHandler(EventHandler parentEventHandler, ControllerMethod controllerMethod) {
        this.parentEventHandler = parentEventHandler;
        this.controllerMethod = controllerMethod;
        this.level = 0;
    }

    @Override
    public boolean matches(XMLEvent event, ExecutionContext context) {
        switch (event.getEventType()) {
            case START_ELEMENT:
                return isRecursionStart(event) || context.isNodeFlagged(event.asStartElement().getName());
            case END_ELEMENT:
                return controllerMethod.isMatchingEndElement(event.asEndElement());
            default:
                return false;
        }
    }

    @Override
    public EventHandler process(XMLEvent event, ExecutionContext context) {
        switch (event.getEventType()) {
            case START_ELEMENT:
                if (isRecursionStart(event)) {
                    level++;
                    controllerMethod.processStartElement(event.asStartElement(), context);
                    return this;
                } else {
                    return new NodeContentEventHandler(
                            this, controllerMethod.getOwnerName(), event.asStartElement().getName());
                }
            case END_ELEMENT:
                controllerMethod.processEndElement(context);
                if (isRecursionFinish()) {
                    return parentEventHandler;
                } else {
                    level--;
                    return this;
                }
            default:
                throw new IllegalStateException("Unsupported event type: " + event.getEventType());
        }
    }

    private boolean isRecursionStart(XMLEvent event) {
        return controllerMethod.isMatchingStartElement(event.asStartElement());
    }

    private boolean isRecursionFinish() {
        return level == 0;
    }
}