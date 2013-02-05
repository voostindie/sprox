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

package nl.ulso.sprox.atom;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

import static nl.ulso.sprox.atom.TextType.*;

public class TextTypeParser implements Parser<TextType> {
    @Override
    public TextType fromString(String value) throws ParseException {
        switch (value.toLowerCase()) {
            case "text":
                return TEXT;
            case "html":
                return HTML;
            case "xhtml":
                return XHTML;
            default:
                throw new ParseException(TextType.class, value);
        }
    }
}
