package nl.ulso.sprox.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import java.util.Optional;

/**
 * Represents a parameter whose value corresponds with the contents of a node.
 */
final class NodeControllerParameter implements ControllerParameter {
    private final QName ownerName;
    private final QName nodeName;
    private final Class type;
    private final boolean optional;

    NodeControllerParameter(QName ownerName, QName nodeName, Class type, boolean optional) {
        this.ownerName = ownerName;
        this.nodeName = nodeName;
        this.type = type;
        this.optional = optional;
    }

    @Override
    public boolean isValidStartElement(StartElement node) {
        return true;
    }

    @Override
    public void pushToExecutionContext(StartElement node, ExecutionContext context) {
        context.flagNode(ownerName, nodeName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> resolveMethodParameter(ExecutionContext context) {
        return context.getNodeContent(ownerName, nodeName)
                .flatMap(value -> Optional.ofNullable(context.parseString((String) value, type)));
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
