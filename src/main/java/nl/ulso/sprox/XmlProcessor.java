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

import java.io.InputStream;
import java.io.Reader;

/**
 * Processes an XML from some source, resulting in an object of type {@code T}.
 * <p>
 * Processors are immutable, and thread-safe. That means: a single instance may safely be used across multiple threads
 * concurrently. Note that this statement only holds if all controllers are thread-safe as well. That's up to you!
 * </p>
 * <p>
 * To create a processor, use an {@link XmlProcessorBuilder}, obtained through an {@link XmlProcessorBuilderFactory}.
 * </p>
 *
 * @see XmlProcessorBuilder
 * @see XmlProcessorBuilderFactory
 */
public interface XmlProcessor<T> {

    /**
     * Process XML by pulling it from a reader. The reader is not closed. If buffering is needed, this must be
     * provided by the caller.
     *
     * @param reader The reader to pull the XML from.
     * @return The result of processing the XML; is {@code null} only if {@code T} is {@link java.lang.Void}.
     * @throws XmlProcessorException If an error occurred while processing the XML
     */
    T execute(Reader reader) throws XmlProcessorException;

    /**
     * Process XML by pulling it from an input stream. The input stream is not closed. If buffering is needed, this
     * must be provided by the caller.
     *
     * @param inputStream The stream to pull the XML from.
     * @return The result of processing the XML; is {@code null} only if {@code T} is {@link java.lang.Void}.
     * @throws XmlProcessorException If an error occurred while processing the XML
     */
    T execute(InputStream inputStream) throws XmlProcessorException;
}
