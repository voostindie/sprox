package nl.ulso.sprox.impl;

import javax.xml.stream.events.XMLEvent;

/**
 * Defines the interface for handling events in the XML stream.
 * <p>
 * The {@link nl.ulso.sprox.impl.StaxBasedXmlProcessor} goes through the XML stream event by event and executes the
 * first (best) matching event handler.
 */
interface EventHandler {
    /**
     * Verifies whether this event handler applies to the specified event.
     *
     * @param event   Event to test against.
     * @param context The current execution context.
     * @return {@code true} if this handler matches, {@code false} if not.
     */
    boolean matches(XMLEvent event, ExecutionContext context);

    /**
     * Processes an event and returns a followup event handler. Called only if this handler {@link #matches} the event.
     *
     * @param event   Event to process.
     * @param context The current execution context.
     * @return The followup event handler.
     */
    EventHandler process(XMLEvent event, ExecutionContext context);
}
