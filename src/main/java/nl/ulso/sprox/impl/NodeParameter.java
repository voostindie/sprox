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
import javax.xml.stream.events.StartElement;

final class NodeParameter implements Parameter {
    private final QName owner;
    private final QName name;
    private final Class type;
    private final boolean required;

    NodeParameter(QName owner, QName name, Class type, boolean required) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.required = required;
    }

    @Override
    public boolean isValidStartElement(StartElement node) {
        return true;
    }

    @Override
    public void pushToExecutionContext(StartElement node, ExecutionContext context) {
        context.flagNode(owner, name);
    }

    @Override
    public Object resolveMethodParameter(ExecutionContext context) throws ParseException {
        final String value = context.getNodeValue(owner, name);
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
