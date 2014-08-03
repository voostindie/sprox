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

package nl.ulso.sprox.parsers;

import nl.ulso.sprox.Parser;
import nl.ulso.sprox.ParseException;

/**
 * Simple no-op parser that returns the string value itself.
 * <p>
 * Reasons for having this implementation:
 * </p>
 * <ul>
 * <li>It makes the implementation of the processor easier. Strings are not a special case.</li>
 * <li>It allows developers to replace this parser with their own.</li>
 * </ul>
 */
public class StringParser implements Parser<String> {
    @Override
    public String fromString(String value) throws ParseException {
        return value;
    }
}
