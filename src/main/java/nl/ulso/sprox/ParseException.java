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
 * Exception thrown when parsing a string into a specific type failed.
 */
public final class ParseException extends Exception {
    public ParseException(Class resultClass, String value) {
        super(createMessage(resultClass, value));
    }

    public ParseException(Class resultClass, String value, Throwable cause) {
        super(createMessage(resultClass, value), cause);
    }

    private static String createMessage(Class resultClass, String value) {
        return "Could not parse string \"" + value + "\" into a value of type \"" + resultClass + "\"";
    }
}
