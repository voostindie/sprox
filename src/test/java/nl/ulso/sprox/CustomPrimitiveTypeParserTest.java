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

import org.junit.Test;

import static nl.ulso.sprox.XmlProcessorFactory.*;

public class CustomPrimitiveTypeParserTest {

    @Test
    public void testCustomIntegerParser() {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerClass(IntegerParser.class)
                .addParser(new Parser<Integer>() {
                    @Override
                    public Integer fromString(String value) throws ParseException {
                        return Math.abs(Integer.parseInt(value));
                    }
                })
                .buildXmlProcessor();
        SproxTests.testProcessor("42", "<number value=\"-42\"/>", processor);
    }

    public static final class IntegerParser {
        @Node("number")
        public String getInteger(@Attribute("value") Integer value) {
            return value.toString();
        }
    }
}