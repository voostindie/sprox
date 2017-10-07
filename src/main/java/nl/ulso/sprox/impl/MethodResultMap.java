package nl.ulso.sprox.impl;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * Keeps track of method results produced by controller methods for the {@link ExecutionContext}.
 * <p/>
 * Method results are collected whenever they are produced and can be injected anywhere.
 * <p/>
 * For each result type this class keeps of list of objects of that type produced. With each object it also stores
 * for which node it was produced and at what depth in the XML this was. Later, when the method results are popped from
 * the map, only those method results found at a lower depth than the current depth and belonging to the correct source
 * node (if any) are collected.
 *
 * @see ExecutionContext
 */
final class MethodResultMap {
    /*
     * Key: result type
     * Value: list of method results of this type
     */
    private final Map<Class, List<MethodResult>> methodResults;

    MethodResultMap() {
        methodResults = new HashMap<>();
    }

    void put(int depth, QName owner, Class objectClass, Object value) {
        if (!methodResults.containsKey(objectClass)) {
            methodResults.put(objectClass, new ArrayList<>());
        }
        methodResults.get(objectClass).add(new MethodResult(depth, owner, value));
    }

    Optional<List<?>> pop(int depth, QName sourceName, Class objectClass) {
        final List<MethodResult> results = methodResults.get(objectClass);
        if (results == null) {
            return Optional.empty();
        }
        final List<Object> list = new ArrayList<>(results.size());
        final Iterator<MethodResult> iterator = results.iterator();
        while (iterator.hasNext()) {
            final MethodResult methodResult = iterator.next();
            if (methodResult.depth <= depth) {
                continue;
            }
            if (sourceName == null || sourceName.equals(methodResult.sourceName)) {
                list.add(methodResult.value);
                iterator.remove();
            }
        }
        if (results.isEmpty()) {
            methodResults.remove(objectClass);
        }
        return Optional.of(list);
    }

    private static final class MethodResult {
        private final int depth;
        private final QName sourceName;
        private final Object value;

        private MethodResult(int depth, QName sourceName, Object value) {
            this.depth = depth;
            this.sourceName = sourceName;
            this.value = value;
        }
    }
}
