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

package nl.ulso.sprox.opml;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Rfc822DateTimeParser implements Parser<DateTime> {

    // Not sure if this pattern is completely correct, but all dates in the test data are parsed correctly.
    private static final DateTimeFormatter parser = DateTimeFormat.forPattern("EEE, dd MMM YYYY HH:mm:ss ZZZ");

    public DateTime fromString(String value) throws ParseException {
        try {
            return parser.parseDateTime(value);
        } catch (IllegalArgumentException e) {
            throw new ParseException(DateTime.class, value, e);
        }
    }
}
