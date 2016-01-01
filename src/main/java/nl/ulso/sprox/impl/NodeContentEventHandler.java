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
