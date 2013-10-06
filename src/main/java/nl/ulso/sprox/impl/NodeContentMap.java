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

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of nodes in the XML that need to be injected later, for the {@link ExecutionContext}.
 * <p/>
 * Of all the types collected - also see {@link AttributeMap} and {@link MethodResultMap} - this is the most complex.
 * Nodes may be collected at multiple levels and injected several levels up.
 *
 * @see {@link ExecutionContext}
 */
final class NodeContentMap {
    /*
     * Key: node that triggered the collection of specific node contents
     * Value: pairs of node names and their content.
     */
    private final Map<OwnerNode, Map<QName, NodeContent>> nodes;

    NodeContentMap() {
        nodes = new HashMap<>();
    }

    void flag(int depth, QName ownerName, QName nodeName) {
        final OwnerNode ownerNode = new OwnerNode(depth, ownerName);
        if (!nodes.containsKey(ownerNode)) {
            nodes.put(ownerNode, new HashMap<QName, NodeContent>());
        }
        nodes.get(ownerNode).put(nodeName, null);
    }

    boolean isFlagged(QName nodeName) {
        for (Map<QName, NodeContent> nodeContentMap : nodes.values()) {
            if (nodeContentMap.containsKey(nodeName)) {
                return true;
            }
        }
        return false;
    }

    void put(int depth, QName ownerName, QName nodeName, String nodeValue) {
        final Map<QName, NodeContent> nodeContentMap = findNodeContentMap(depth, ownerName);
        final NodeContent nodeContent = nodeContentMap.get(nodeName);
        if (nodeContent == null || nodeContent.depth >
                depth) {
            nodeContentMap.put(nodeName, new NodeContent(depth, nodeValue));
        }
    }

    private Map<QName, NodeContent> findNodeContentMap(int depth, QName ownerName) {
        for (int i = depth; i > 0; i--) {
            final OwnerNode ownerNode = new OwnerNode(i, ownerName);
            if (nodes.containsKey(ownerNode)) {
                return nodes.get(ownerNode);
            }
        }
        throw new IllegalStateException("Could not find a map of nodes collected for " + ownerName
                + ". That's a bug!. The owner node should have been flagged earlier.");
    }

    String get(int depth, QName ownerName, QName nodeName) {
        final OwnerNode ownerNode = new OwnerNode(depth, ownerName);
        final NodeContent nodeContent = nodes.get(ownerNode).get(nodeName);
        return nodeContent != null ? nodeContent.content : null;
    }

    void clear(int depth, QName ownerName) {
        final OwnerNode ownerNode = new OwnerNode(depth, ownerName);
        nodes.remove(ownerNode);
    }

    private static final class OwnerNode {
        private final int depth;
        private final QName name;

        private OwnerNode(int depth, QName name) {
            this.depth = depth;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OwnerNode ownerNode = (OwnerNode) o;
            return depth == ownerNode.depth && name.equals(ownerNode.name);
        }

        @Override
        public int hashCode() {
            int result = depth;
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    private static final class NodeContent {
        private final int depth;
        private final String content;

        private NodeContent(int depth, String content) {
            this.depth = depth;
            this.content = content;
        }
    }
}
