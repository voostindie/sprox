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

package nl.ulso.sprox;

/**
 * Defines the builder interface for setting up an {@link XmlProcessor}.
 * <p>
 * A single builder can be reused to create multiple independent processors.
 * </p>
 * <p>
 * To obtain a builder, use an {@link XmlProcessorBuilderFactory}.
 * </p>
 *
 * @see XmlProcessorBuilderFactory
 * @see XmlProcessor
 */
public interface XmlProcessorBuilder<T> {

    /**
     * Adds a controller object. The processor built by this builder invokes method on this object. It is used
     * as a singleton. It must be thread-safe!
     *
     * @param controller Object to add as a controller.
     * @return This builder.
     * @throws IllegalArgumentException If a controller for this controller's class is already registered.
     */
    XmlProcessorBuilder<T> addControllerObject(Object controller);

    /**
     * Adds a controller class. The processor built by this builder creates exactly one instance of this class
     * for every execution. The class doesn't need to be thread-safe. It must have a no-arg constructor.
     *
     * @param controllerClass The class to add as a controller class.
     * @return This builder.
     * @throws IllegalArgumentException If a controller for this class is already registered.
     */
    XmlProcessorBuilder<T> addControllerClass(Class controllerClass);

    /**
     * Adds a controller factory. The processor built by this builder calls the factory exactly once for every
     * execution.
     *
     * @param controllerFactory The factory to add as a controller factory.
     * @return This builder.
     * @throws IllegalArgumentException If a controller for this controller's class is already registered.
     */
    XmlProcessorBuilder<T> addControllerFactory(ControllerFactory<?> controllerFactory);

    /**
     * Adds a single parser. {@link Parser}s are used to convert {@code String} values for nodes and attributes
     * to other types. For every type there can be at most one parser. Adding a parser for a type that already has
     * a parser registered is not an error. Instead the parser is replaced with the new one.
     *
     * @param parser The parser to add.
     * @return This builder.
     */
    XmlProcessorBuilder<T> addParser(Parser<?> parser);

    /**
     * Builds an {@link XmlProcessor}.
     * <p>
     * The processor created by this builder is immutable and thread-safe. This builder may safely be modified or
     * thrown away.
     * </p>
     *
     * @return a new {@link XmlProcessor}.
     * @throws IllegalStateException If the processor could not be built because its configuration is incorrect.
     * @see XmlProcessor
     */
    XmlProcessor<T> buildXmlProcessor();
}
