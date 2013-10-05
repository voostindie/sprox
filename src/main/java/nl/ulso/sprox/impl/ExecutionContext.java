/*
 * Copyright 2013 Vincent Oostindië
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.sprox.impl;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * Container for all data collected during XML processing. This class is the only class in the system that builds up
 * state during an XML processing run.
 * <p/>
 * Controller methods are called after the associated end element is found. All data that must be injected is collected
 * during the processing of the elements within that element. This class keeps track of the data that is collected,
 * and also ensures that it collects only the data that it really needs to collect.
 * <p/>
 * Different types of data can be injected, and each type must be collected a little differently:
 * <ul>
 * <li><strong>Attributes</strong>: These are known as soon as the start element is found. They only need to
 * be stored temporarily. When stored they must be bound to a location because the context might contain attributes
 * of many nodes that have been processed. The interesting case here is a recursive node structure, like:
 * <pre>
 *     &lt;node attribute="1"&gt;
 *         &lt;node attribute="2"/&gt;
 *     &lt;/node&gt;
 * </pre>
 * When invoking the controller method for the inner node, the injected attribute must have the value {@code 2}, while
 * the outer node requires {@code 1}. That's why this context keeps track of the depth where the attributes were found.
 * At any time, attributes for at most one node need to be stored at a specific depth.
 * </li>
 * <li><strong>Nodes</strong>: When node content must be read, the processor basically switches over to a
 * another strategy: it simply collects all the data under the node, until the matching end element is found. An
 * interesting case is that the element we're interested in might be available many times, for example:
 * <pre>
 *     &lt;root&gt;
 *         &lt;node&gt;value1&lt;/node&gt;
 *         &lt;subnode&gt;
 *             &lt;node&gt;value2&lt;/node&gt;
 *         &lt;/subnode&gt;
 *         &lt;node&gt;value3&lt;/node&gt;
 *     &lt;/root&gt;
 * </pre>
 * By keeping track of the level that the content was found at and only keeping data found at the lowest level, at
 * most once, the processor ensures that the result is predictable (and logical). In this case: the result is
 * {@code value1}, the value closest to the root.</li>
 * <li><strong>Objects</strong>: Objects are created by invoking controller methods. When created, they are stored.
 * The hierarchy of the processed XML doesn't need to match the hierarchy of the controllers: an object may be injected
 * several levels up, with other controller methods on intermediate levels that ignore it.</li>
 * <li><strong>Result</strong>: The processing result is just an object created from a controller method. The last
 * object created by any method with the correct result type is considered to be the processing result. Typically
 * the result is produced by a method annotated with the root node. This method is always called last.</li>
 * </ul>
 */
final class ExecutionContext<T> {
    private final Class<T> resultClass;
    private final Map<Class, Object> controllers;
    private final Map<Class<?>, Parser<?>> parsers;
    private final Set<QName> flaggedNodes;
    private final AttributeMap attributes;
    private final Map<QName, Map<QName, NodeBody>> nodes;
    private final Map<Class, List<MethodResult>> methodResults;
    private int currentDepth;
    private T result;

    ExecutionContext(Class<T> resultClass, Map<Class, Object> controllers, Map<Class<?>, Parser<?>> parsers) {
        this.resultClass = resultClass;
        this.controllers = controllers;
        this.parsers = parsers;
        this.flaggedNodes = new HashSet<>();
        this.attributes = new AttributeMap();
        this.nodes = new HashMap<>();
        this.methodResults = new HashMap<>();
        this.currentDepth = 0;
        this.result = null;
    }

    Object getController(Class controllerClass) {
        return controllers.get(controllerClass);
    }

    <T> T parseString(String value, Class<T> resultClass) throws ParseException {
        @SuppressWarnings("unchecked")
        final Parser<T> parser = (Parser<T>) parsers.get(resultClass);
        if (parser == null) {
            throw new IllegalStateException("No parser available for type: " + resultClass);
        }
        return parser.fromString(value);
    }

    void flagNode(QName node) {
        flaggedNodes.add(node);
    }

    boolean isNodeFlagged(QName node) {
        return flaggedNodes.contains(node);
    }

    void pushAttribute(QName attributeName, String attributeValue) {
        attributes.put(currentDepth, attributeName, attributeValue);
    }

    String getAttributeValue(QName name) {
        return attributes.get(currentDepth, name);
    }

    private void removeAttributes() {
        attributes.clear(currentDepth);
    }

    void pushNode(QName owner, QName nodeName, String nodeValue) {
        if (!nodes.containsKey(owner)) {
            nodes.put(owner, new HashMap<QName, NodeBody>());
        }
        final Map<QName, NodeBody> bodyMap = nodes.get(owner);
        final NodeBody nodeBody = bodyMap.get(nodeName);
        if (nodeBody == null || nodeBody.depth > currentDepth) {
            bodyMap.put(nodeName, new NodeBody(currentDepth, nodeValue));
        }
    }

    String getNodeValue(QName owner, QName name) {
        if (!nodes.containsKey(owner)) {
            return null;
        }
        final NodeBody nodeBody = nodes.get(owner).get(name);
        return nodeBody != null ? nodeBody.content : null;
    }

    void removeAttributesAndNodes(QName owner) {
        removeAttributes();
        if (nodes.containsKey(owner)) {
            final Map<QName, NodeBody> bodyMap = nodes.remove(owner);
            for (QName name : bodyMap.keySet()) {
                flaggedNodes.remove(name);
            }
        }
    }

    @SuppressWarnings("unchecked")
    void pushMethodResult(QName owner, Class objectClass, Object value) {
        if (!methodResults.containsKey(objectClass)) {
            methodResults.put(objectClass, new ArrayList<MethodResult>());
        }
        methodResults.get(objectClass).add(new MethodResult(currentDepth, owner, value));
        if (resultClass.equals(objectClass)) {
            result = (T) value;
        }
    }

    List<?> popMethodResults(QName sourceNode, Class objectClass) {
        final List<MethodResult> results = methodResults.get(objectClass);
        if (results == null) {
            return null;
        }
        final List<Object> list = new ArrayList<>(results.size());
        final Iterator<MethodResult> iterator = results.iterator();
        while (iterator.hasNext()) {
            final MethodResult methodResult = iterator.next();
            if (methodResult.depth <= currentDepth) {
                continue;
            }
            if (sourceNode == null || sourceNode.equals(methodResult.sourceNode)) {
                list.add(methodResult.value);
                iterator.remove();
            }
        }
        if (results.isEmpty()) {
            methodResults.remove(objectClass);
        }
        return list;
    }

    void increaseDepth() {
        currentDepth++;
    }

    void decreaseDepth() {
        currentDepth--;
    }

    T getResult() {
        return this.result;
    }

    private static final class NodeBody {
        private final int depth;
        private final String content;

        private NodeBody(int depth, String body) {
            this.depth = depth;
            this.content = body;
        }
    }

    private static final class MethodResult {
        private final int depth;
        private final QName sourceNode;
        private final Object value;

        private MethodResult(int depth, QName sourceNode, Object value) {
            this.depth = depth;
            this.sourceNode = sourceNode;
            this.value = value;
        }
    }
}
