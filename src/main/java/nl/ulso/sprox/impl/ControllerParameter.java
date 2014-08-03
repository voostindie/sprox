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

import javax.xml.stream.events.StartElement;
import java.util.Optional;

/**
 * Represents a parameter in a method that needs to be injected.
 */
interface ControllerParameter {

    /**
     * Validates this parameter for the start element.
     *
     * @param node The start element to validate.
     * @return {@code true} if this parameter can be processed correctly, {@code false} otherwise.
     */
    boolean isValidStartElement(StartElement node);

    /**
     * Pushes all relevant data for the parameter from the start element to the execution context; only called
     * if {@link #isValidStartElement(javax.xml.stream.events.StartElement)} returned {@code true}.
     *
     * @param node    Node to push the parameter for.
     * @param context The current execution context.
     */
    void pushToExecutionContext(StartElement node, ExecutionContext context);

    /**
     * Resolves the value of the parameter from the execution context.
     *
     * @param context The current execution context.
     * @return The value of the parameter.
     */
    Optional<Object> resolveMethodParameter(ExecutionContext context);

    /**
     * @return Whether this parameter is an {@link java.util.Optional} parameter.
     */
    boolean isOptional();
}
