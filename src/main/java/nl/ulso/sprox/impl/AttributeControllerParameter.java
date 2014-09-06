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
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import java.util.Optional;

/**
 * Represents a parameter whose value must be pulled from a node attribute.
 */
final class AttributeControllerParameter implements ControllerParameter {
    private final QName name;
    private final QName localName;
    private final Class type;
    private final boolean optional;

    AttributeControllerParameter(QName name, Class type, boolean optional) {
        this.name = name;
        this.localName = new QName(name.getLocalPart());
        this.type = type;
        this.optional = optional;
    }

    @Override
    public boolean isValidStartElement(StartElement node) {
        return optional || findAttribute(node).isPresent();
    }

    @Override
    public void pushToExecutionContext(StartElement node, ExecutionContext context) {
        findAttribute(node).ifPresent(attribute -> context.pushAttribute(name, attribute.getValue()));
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> resolveMethodParameter(ExecutionContext context) {
        return context.getAttributeValue(name)
                .map(value -> context.parseString((String) value, type));
    }

    private Optional<Attribute> findAttribute(StartElement node) {
        if (name.getNamespaceURI() != null && name.getNamespaceURI().equals(node.getName().getNamespaceURI())) {
            return Optional.ofNullable(node.getAttributeByName(localName));
        }
        return Optional.ofNullable(node.getAttributeByName(name));
    }
}
