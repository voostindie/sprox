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
 * Keeps track of attributes in the XML that need to be injected later, for the {@link ExecutionContext}.
 * <p/>
 * At any depth in the XML, there's at most one list of attributes to keep track of.
 */
final class AttributeMap {

    private final Map<Integer, Map<QName, String>> attributes;

    AttributeMap() {
        attributes = new HashMap<>();
    }

    void put(int depth, QName name, String value) {
        if (!attributes.containsKey(depth)) {
            attributes.put(depth, new HashMap<QName, String>());
        }
        attributes.get(depth).put(name, value);
    }

    String get(int depth, QName name) {
        final Map<QName, String> map = attributes.get(depth);
        return map != null ? map.get(name) : null;
    }

    void clear(int depth) {
        attributes.remove(depth);
    }
}
