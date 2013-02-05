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
 * Parses a string from a node or an attribute into a specific type.
 * <p/>
 * Sprox has built-in parsers for all primitive types. By implementing this interface and registering it through the
 * {@link XmlProcessorBuilder} you can add your own.
 * <p/>
 * When implementing a parser, make sure they are thread-safe. {@link XmlProcessor}s can be be used concurrently.
 */
public interface Parser<T> {

    /**
     * Parses a string value into a specific type.
     *
     * @param value String value to parse; never {@code null}
     * @return The result of parsing the value; may not be {@code null}.
     * @throws ParseException If the value could not be parsed.
     */
    T fromString(String value) throws ParseException;
}
