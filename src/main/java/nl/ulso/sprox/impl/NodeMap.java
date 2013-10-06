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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of nodes in the XML that need to be injected later, for the {@link ExecutionContext}.
 */
final class NodeMap {

    private final Set<QName> flaggedNodes;
    private final Map<QName, Map<QName, NodeBody>> nodes;

    NodeMap() {
        flaggedNodes = new HashSet<>();
        nodes = new HashMap<>();
    }

    public void flag(QName node) {
        flaggedNodes.add(node);

    }

    public boolean isFlagged(QName node) {
        return flaggedNodes.contains(node);
    }

    public void put(int currentDepth, QName owner, QName nodeName, String nodeValue) {
        if (!nodes.containsKey(owner)) {
            nodes.put(owner, new HashMap<QName, NodeBody>());
        }
        final Map<QName, NodeBody> bodyMap = nodes.get(owner);
        final NodeBody nodeBody = bodyMap.get(nodeName);
        if (nodeBody == null || nodeBody.depth > currentDepth) {
            bodyMap.put(nodeName, new NodeBody(currentDepth, nodeValue));
        }
    }

    public String get(QName owner, QName name) {
        if (!nodes.containsKey(owner)) {
            return null;
        }
        final NodeBody nodeBody = nodes.get(owner).get(name);
        return nodeBody != null ? nodeBody.content : null;
    }

    public void clear(QName owner) {
        if (nodes.containsKey(owner)) {
            final Map<QName, NodeBody> bodyMap = nodes.remove(owner);
            for (QName name : bodyMap.keySet()) {
                flaggedNodes.remove(name);
            }
        }
    }

    private static final class NodeBody {
        private final int depth;
        private final String content;

        private NodeBody(int depth, String body) {
            this.depth = depth;
            this.content = body;
        }
    }
}
