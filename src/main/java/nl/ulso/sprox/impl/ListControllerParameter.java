package nl.ulso.sprox.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import java.util.Optional;

/**
 * Represents a parameter whose value is a list of collected method results.
 */
final class ListControllerParameter implements ControllerParameter {
    private final Class elementClass;
    private final QName sourceName;
    private final boolean optional;

    ListControllerParameter(Class elementClass, QName sourceName, boolean optional) {
        this.elementClass = elementClass;
        this.sourceName = sourceName;
        this.optional = optional;
    }

    @Override
    public boolean isValidStartElement(StartElement node) {
        return true;
    }

    @Override
    public void pushToExecutionContext(StartElement node, ExecutionContext context) {
        // Nothing to do here.
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> resolveMethodParameter(ExecutionContext context) {
        return context.popMethodResults(sourceName, elementClass);
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
