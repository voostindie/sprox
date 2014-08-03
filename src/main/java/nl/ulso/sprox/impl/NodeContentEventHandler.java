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

import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Event handler that collects all content in a node.
 */
final class NodeContentEventHandler implements EventHandler {
    private final QName ownerName;
    private final QName nodeName;
    private final EventHandler parentEventHandler;

    NodeContentEventHandler(EventHandler parentEventHandler, QName ownerName, QName nodeName) {
        this.ownerName = ownerName;
        this.nodeName = nodeName;
        this.parentEventHandler = parentEventHandler;
    }

    @Override
    public boolean matches(XMLEvent event, ExecutionContext context) {
        switch (event.getEventType()) {
            case START_ELEMENT:
                return true;
            case CHARACTERS:
                return true;
            case END_ELEMENT:
                return true;
            default:
                throw new IllegalStateException("Illegal event for @Node injection: " + event.getLocation());
        }
    }

    @Override
    public EventHandler process(XMLEvent event, ExecutionContext context) {
        switch (event.getEventType()) {
            case START_ELEMENT:
                if (parentEventHandler.matches(event, context)) {
                    return parentEventHandler.process(event, context);
                }
                return parentEventHandler;
            case CHARACTERS:
                context.pushNodeContent(ownerName, nodeName, event.asCharacters().getData());
                return this;
            case END_ELEMENT:
                if (parentEventHandler.matches(event, context)) {
                    return parentEventHandler.process(event, context);
                } else {
                    return parentEventHandler;
                }
            default:
                throw new IllegalStateException("Unsupported event type: " + event.getEventType());
        }
    }
}
