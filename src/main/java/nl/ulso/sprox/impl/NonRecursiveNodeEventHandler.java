package nl.ulso.sprox.impl;

import javax.xml.stream.events.XMLEvent;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Event handler for non-recursive nodes.
 */
final class NonRecursiveNodeEventHandler implements EventHandler {
    private final EventHandler parentEventHandler;
    private final ControllerMethod controllerMethod;

    NonRecursiveNodeEventHandler(EventHandler parentEventHandler, ControllerMethod controllerMethod) {
        this.parentEventHandler = parentEventHandler;
        this.controllerMethod = controllerMethod;
    }

    @Override
    public boolean matches(XMLEvent event, ExecutionContext context) {
        switch (event.getEventType()) {
            case START_ELEMENT:
                return context.isNodeFlagged(event.asStartElement().getName());
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
                return new NodeContentEventHandler(
                        this, controllerMethod.getOwnerName(), event.asStartElement().getName());
            case END_ELEMENT:
                controllerMethod.processEndElement(context);
                return parentEventHandler;
            default:
                throw new IllegalStateException("Unsupported event type: " + event.getEventType());
        }
    }
}
