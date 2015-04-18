/*
 * Copyright 2013-2015 Vincent Oostindië
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

import nl.ulso.sprox.ParseException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class BooleanParserTest {

    private BooleanParser parser = new BooleanParser();

    @Test
    public void testOneIsTrue() throws Exception {
        final Boolean result = parser.fromString("1");
        assertThat(result, is(true));
    }

    @Test
    public void testTrueIsTrue() throws Exception {
        final Boolean result = parser.fromString("true");
        assertThat(result, is(true));
    }

    @Test
    public void testZeroIsFalse() throws Exception {
        final Boolean result = parser.fromString("0");
        assertThat(result, is(false));
    }

    @Test
    public void testFalseIsFalse() throws Exception {
        final Boolean result = parser.fromString("false");
        assertThat(result, is(false));
    }

    @Test(expected = ParseException.class)
    public void testExceptionForUnknownData() throws Exception {
        parser.fromString("foo");
    }
}