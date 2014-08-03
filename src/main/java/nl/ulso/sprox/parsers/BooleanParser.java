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
 * Parses an `xsd:boolean` into a Boolean.
 */
public class BooleanParser implements Parser<Boolean> {
    @Override
    public Boolean fromString(String value) throws ParseException {
        if (value.equals("true") || value.equals("1")) {
            return Boolean.TRUE;
        }
        if (value.equals("false") || value.equals("0")) {
            return Boolean.FALSE;
        }
        throw new ParseException(Boolean.class, value);
    }
}
