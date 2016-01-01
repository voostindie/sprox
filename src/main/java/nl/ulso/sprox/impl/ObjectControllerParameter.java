package nl.ulso.sprox.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import java.util.List;
import java.util.Optional;

/**
 * Represents a parameter whose value is a collected method result.
 */
class ObjectControllerParameter implements ControllerParameter {
    private final Class objectClass;
    private final QName sourceName;
    private final boolean optional;

    ObjectControllerParameter(Class objectClass, QName sourceName, boolean optional) {
        this.objectClass = objectClass;
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
        return context.popMethodResults(sourceName, objectClass)
                .filter(list -> !((List<?>) list).isEmpty())
                .map(list -> ((List<?>) list).get(0));
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
