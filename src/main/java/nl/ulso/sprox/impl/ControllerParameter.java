package nl.ulso.sprox.impl;

import javax.xml.stream.events.StartElement;
import java.util.Optional;

/**
 * Represents a parameter in a method that needs to be injected.
 */
interface ControllerParameter {

    /**
     * Validates this parameter for the start element.
     *
     * @param node The start element to validate.
     * @return {@code true} if this parameter can be processed correctly, {@code false} otherwise.
     */
    boolean isValidStartElement(StartElement node);

    /**
     * Pushes all relevant data for the parameter from the start element to the execution context; only called
     * if {@link #isValidStartElement(javax.xml.stream.events.StartElement)} returned {@code true}.
     *
     * @param node    Node to push the parameter for.
     * @param context The current execution context.
     */
    void pushToExecutionContext(StartElement node, ExecutionContext context);

    /**
     * Resolves the value of the parameter from the execution context.
     *
     * @param context The current execution context.
     * @return The value of the parameter.
     */
    Optional<Object> resolveMethodParameter(ExecutionContext context);

    /**
     * @return Whether this parameter is an {@link java.util.Optional} parameter.
     */
    boolean isOptional();
}
