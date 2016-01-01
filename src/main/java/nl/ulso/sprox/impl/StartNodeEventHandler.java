package nl.ulso.sprox.impl;

import javax.xml.stream.events.XMLEvent;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Event handler for start nodes. For every controller method there's exactly one such event handler. At the start of
 * the execution of an {@link nl.ulso.sprox.impl.StaxBasedXmlProcessor} it has a list of {@code StartNodeEventHandler}s
 * only.
 */
final class StartNodeEventHandler implements EventHandler {
    private final ControllerMethod controllerMethod;
    private final EventHandler nodeEventHandler;

    StartNodeEventHandler(ControllerMethod controllerMethod, boolean recursive) {
        this.controllerMethod = controllerMethod;
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
