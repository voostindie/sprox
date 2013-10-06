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

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

/**
 * Represents a parameter whose value must be pulled from a node attribute.
 */
final class AttributeControllerParameter implements ControllerParameter {
    private final QName name;
    private final QName localName;
    private final Class type;
    private final boolean required;

    AttributeControllerParameter(QName name, Class type, boolean required) {
        this.name = name;
        this.localName = new QName(name.getLocalPart());
        this.type = type;
        this.required = required;
    }

    @Override
    public boolean isValidStartElement(StartElement node) {
        return !(findAttribute(node) == null && required);
    }

    @Override
    public void pushToExecutionContext(StartElement node, ExecutionContext context) {
        final Attribute attribute = findAttribute(node);
        if (attribute != null) {
            context.pushAttribute(name, attribute.getValue());
        }
    }

    private Attribute findAttribute(StartElement node) {
        if (name.getNamespaceURI() != null && name.getNamespaceURI().equals(node.getName().getNamespaceURI())) {
            return node.getAttributeByName(localName);
        }
        return node.getAttributeByName(name);
    }

    @Override
    public Object resolveMethodParameter(ExecutionContext context) throws ParseException {
        final String value = context.getAttributeValue(name);
        if (value != null) {
            //noinspection unchecked
            return context.parseString(value, type);
        }
        return null;
    }

    @Override
    public boolean isRequired() {
        return required;
    }
}
