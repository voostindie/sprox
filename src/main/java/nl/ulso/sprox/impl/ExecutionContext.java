/*
 * Copyright 2013-2014 Vincent Oostindië
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static nl.ulso.sprox.impl.UncheckedXmlProcessorException.unchecked;

/**
 * Context object that is unique to each processing run. It is passed between several objects involved in an XML
 * processing run, like {@link EventHandler}s, {@link ControllerMethod}s and {@link ControllerParameter}s.
 * <p/>
 * Controller methods are invoked <strong>after</strong> the associated end element is found. All data that must be
 * injected for the method is collected during the processing of the elements within that element. This class keeps
 * track of the data that is collected, and also ensures that it collects only the data that it really needs to collect.
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
 * the outer node requires {@code 1}. The {@link AttributeMap} is responsible for keeping track of attributes.
 * </li>
 * <li><strong>Nodes</strong>: When node content must be read, the processor switches over to another strategy: it
 * collects all the data under the node, until the matching end element is found. An interesting case is that the
 * element we're interested in might be available many times, for example:
 * <pre>
 *     &lt;root&gt;
 *         &lt;node&gt;value1&lt;/node&gt;
 *         &lt;subnode&gt;
 *             &lt;node&gt;value2&lt;/node&gt;
 *         &lt;/subnode&gt;
 *         &lt;node&gt;value3&lt;/node&gt;
 *     &lt;/root&gt;
 * </pre>
 * In this case: the expected result is {@code value1}, the value closest to the root. The {@link NodeContentMap} is
 * responsible for keeping track of nodes.
 * <li><strong>Method results</strong>: Method results are created by invoking controller methods. When created, they
 * are stored. The hierarchy of the processed XML doesn't need to match the hierarchy of the controllers: an object may
 * be injected several levels up, with other controller methods on intermediate levels that ignore it. The
 * {@link MethodResultMap} is responsible for keeping track of method results.
 * </li>
 * <li><strong>Result</strong>: The processing result is just an object created from a controller method. The last
 * object created by any method with the correct result type is considered to be the processing result. Typically
 * the result is produced by a method annotated with the root node. This method is always called last.</li>
 * </ul>
 *
 * @see AttributeMap
 * @see NodeContentMap
 * @see MethodResultMap
 */
final class ExecutionContext<T> {
    // Immutable data; the same across all processing runs
    private final Class<T> resultClass;
    private final Map<Class, Object> controllers;
    private final Map<Class<?>, Parser<?>> parsers;

    // Mutable data, collected during a single processing run
    private final AttributeMap attributeMap;
    private final MethodResultMap methodResultMap;
    private final NodeContentMap nodeContentMap;
    private int depth;
    private T result;

    ExecutionContext(Class<T> resultClass, Map<Class, Object> controllers, Map<Class<?>, Parser<?>> parsers) {
        this.resultClass = resultClass;
        this.controllers = controllers;
        this.parsers = parsers;
        this.attributeMap = new AttributeMap();
        this.nodeContentMap = new NodeContentMap();
        this.methodResultMap = new MethodResultMap();
        this.depth = 0;
        this.result = null;
    }

    Object getController(Class controllerClass) {
        return controllers.get(controllerClass);
    }

    <R> R parseString(String value, Class<R> resultClass) {
        @SuppressWarnings("unchecked")
        final Parser<R> parser = (Parser<R>) parsers.get(resultClass);
        if (parser == null) {
            throw new IllegalStateException("No parser available for type: " + resultClass);
        }
        try {
            return parser.fromString(value);
        } catch (ParseException e) {
            throw unchecked(e);
        }
    }

    void pushAttribute(QName attributeName, String attributeValue) {
        attributeMap.put(depth, attributeName, attributeValue);
    }

    Optional<String> getAttributeValue(QName attributeName) {
        return attributeMap.get(depth, attributeName);
    }

    void flagNode(QName ownerName, QName nodeName) {
        nodeContentMap.flag(depth, ownerName, nodeName);
    }

    boolean isNodeFlagged(QName nodeName) {
        return nodeContentMap.isFlagged(nodeName);
    }

    void pushNodeContent(QName ownerName, QName nodeName, String nodeContent) {
        nodeContentMap.put(depth, ownerName, nodeName, nodeContent);
    }

    Optional<String> getNodeContent(QName ownerName, QName nodeName) {
        return nodeContentMap.get(depth, ownerName, nodeName);
    }

    void removeAttributesAndNodes(QName ownerName) {
        attributeMap.clear(depth);
        nodeContentMap.clear(depth, ownerName);
    }

    @SuppressWarnings("unchecked")
    void pushMethodResult(QName ownerName, Class objectClass, Object methodResult) {
        methodResultMap.put(depth, ownerName, objectClass, methodResult);
        if (resultClass.equals(objectClass)) {
            result = (T) methodResult;
        }
    }

    Optional<List<?>> popMethodResults(QName sourceName, Class objectClass) {
        return methodResultMap.pop(depth, sourceName, objectClass);
    }

    void increaseDepth() {
        depth++;
    }

    void decreaseDepth() {
        depth--;
    }

    Optional<T> getResult() {
        return Optional.ofNullable(result);
    }
}
