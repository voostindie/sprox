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
import java.util.*;

/**
 * Keeps track of method results from controllers for the {@link ExecutionContext}.
 * <p/>
 * Method results are collected whenever they are produced and can be injected anywhere.
 */
final class MethodResultMap {
    private final Map<Class, List<MethodResult>> methodResults;

    MethodResultMap() {
        methodResults = new HashMap<>();
    }

    void put(int depth, QName owner, Class objectClass, Object value) {
        if (!methodResults.containsKey(objectClass)) {
            methodResults.put(objectClass, new ArrayList<MethodResult>());
        }
        methodResults.get(objectClass).add(new MethodResult(depth, owner, value));
    }

    List<?> pop(int depth, QName sourceNode, Class objectClass) {
        final List<MethodResult> results = methodResults.get(objectClass);
        if (results == null) {
            return null;
        }
        final List<Object> list = new ArrayList<>(results.size());
        final Iterator<MethodResult> iterator = results.iterator();
        while (iterator.hasNext()) {
            final MethodResult methodResult = iterator.next();
            if (methodResult.depth <= depth) {
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
