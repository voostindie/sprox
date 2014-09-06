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

import org.junit.Test;

import java.util.StringJoiner;

import static nl.ulso.sprox.SproxTests.testControllers;

public class NodeAttributesFromParameterNamesTest {

    @Test
    public void testNodeAttributesFromParameterNames() throws Exception {
        testControllers(
                "data2:data1",
                "<root value1=\"data1\" value2=\"data2\"/>",
                new NodesAttributesFromParametersProcessor()
        );

    }

    public static class NodesAttributesFromParametersProcessor {
        @Node("root")
        public String getAttribute(@Attribute String value1, @Attribute String value2) {
            return new StringJoiner(":").add(value2).add(value1).toString();
        }
    }
}
