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

package nl.ulso.sprox;

/**
 * Entry point into Sprox: to create a processor you need a builder and to create a builder, you need this factory.
 * <p>
 * Creating a processor to process XML streams into some type {@code T} consists of the following steps:
 * </p>
 * <ul>
 * <li>Obtaining an instance of this {@link XmlProcessorBuilderFactory}</li>
 * <li>Creating an {@link XmlProcessorBuilder} for the type {@code T}, using the factory</li>
 * <li>Configuring the builder, by adding controllers and parsers, the latter being optional</li>
 * <li>Creating an {@link XmlProcessor}.</li>
 * </ul>
 * <p>
 * Once an {@link XmlProcessor} has been created, it can be reused over and over again. Sprox itself is thread-safe.
 * Whether an {@link XmlProcessor} is thread-safe however depends on the controllers that you provide.
 * </p>
 * <p>
 * An implementation of this factory is accessible in multiple ways:
 * </p>
 * <ul>
 * <li>In a plain Java environment: through the {@link java.util.ServiceLoader}</li>
 * <li>In a OSGi environment: as an OSGi service.</li>
 * </ul>
 * <p>
 * As a last resort, you can create an instance of this interface yourself, by instantiating a
 * {@link nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory}.
 * </p>
 *
 * @see XmlProcessorBuilder
 * @see XmlProcessor
 */
public interface XmlProcessorBuilderFactory {
    <T> XmlProcessorBuilder<T> createXmlProcessorBuilder(Class<T> resultClass);
}
