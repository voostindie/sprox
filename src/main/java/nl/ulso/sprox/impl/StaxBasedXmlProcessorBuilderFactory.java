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

import nl.ulso.sprox.XmlProcessorBuilder;
import nl.ulso.sprox.XmlProcessorBuilderFactory;

/**
 * Implements the {@link XmlProcessorBuilderFactory}; this implementation is registered as an OSGi service, and as a
 * service for the {@link java.util.ServiceLoader}.
 */
public class StaxBasedXmlProcessorBuilderFactory implements XmlProcessorBuilderFactory {
    @Override
    public <T> XmlProcessorBuilder<T> createXmlProcessorBuilder(Class<T> resultClass) {
        return new StaxBasedXmlProcessorBuilder<>(resultClass);
    }
}
