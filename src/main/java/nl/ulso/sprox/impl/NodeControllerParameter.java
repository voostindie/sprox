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
                .flatMap(value -> Optional.of(context.parseString((String) value, type)));
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
