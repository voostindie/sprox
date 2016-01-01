package nl.ulso.sprox.impl;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Keeps track of attributes in the XML that need to be injected later for the {@link ExecutionContext}.
 * <p/>
 * At any depth in the XML, there's at most one list of attributes to keep track of. That's because attributes can
 * only be injected in a controller method triggered on the node that contains the attributes.
 *
 * @see {@link ExecutionContext}
 */
final class AttributeMap {
    /*
     * Key: depth the attributes are found at
     * Value: pairs of attribute names and attribute values
     */
    private final Map<Integer, Map<QName, String>> attributes;

    AttributeMap() {
        attributes = new HashMap<>();
    }

    void put(int depth, QName name, String value) {
        if (!attributes.containsKey(depth)) {
            attributes.put(depth, new HashMap<>());
        }
        attributes.get(depth).put(name, value);
    }

    Optional<String> get(int depth, QName name) {
        final Map<QName, String> map = attributes.get(depth);
        return map != null ? Optional.ofNullable(map.get(name)) : Optional.empty();
    }

    void clear(int depth) {
        attributes.remove(depth);
    }
}
