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

import nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilder;

import static java.util.Objects.requireNonNull;

/**
 * Factory for XML processors or builders of XML processors; the main entry point for the Sprox API.
 * <p/>
 * Creating a processor to process XML streams into some type {@code T} consists of the following steps:
 * <ul>
 * <li>Creating an {@link XmlProcessorBuilder} for the type {@code T}, using this {@link XmlProcessorFactory}</li>
 * <li>Configuring the builder, by adding one or more controllers and parsers, the latter being optional</li>
 * <li>Creating an {@link XmlProcessor}.</li>
 * </ul>
 * <p/>
 * Once an {@link XmlProcessor} has been created, it can be reused over and over again. Sprox itself is thread-safe.
 * Whether an {@link XmlProcessor} is thread-safe however depends on the controllers that you provide.
 *
 * @see XmlProcessorBuilder
 * @see XmlProcessor
 */
public final class XmlProcessorFactory {

    private XmlProcessorFactory() {
    }

    /**
     * Creates a builder for an {@link XmlProcessor}.
     *
     * @param resultClass Class of the processing result, may not be {@code null}.
     * @return A new {@link XmlProcessorBuilder}, ready to be configured; never {@code null}.
     */
    public static <T> XmlProcessorBuilder<T> createXmlProcessorBuilder(Class<T> resultClass) {
        return StaxBasedXmlProcessorBuilder.createBuilder(requireNonNull(resultClass));
    }
}
