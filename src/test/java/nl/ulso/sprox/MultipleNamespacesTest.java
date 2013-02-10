package nl.ulso.sprox;/*
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

import org.junit.Test;

import static nl.ulso.sprox.SproxTests.testControllers;

public class MultipleNamespacesTest {

    @Test
    public void testThatProcessingXmlWithMultipleNamespacesWorksCorrectly() throws Exception {
        final String xml = "<root xmlns=\"rootNamespace\" xmlns:n1=\"namespace1\" xmlns:n2=\"namespace2\">" +
                "<n1:node id=\"node1\"><n1:content>content1</n1:content></n1:node>" +
                "<n2:node id=\"node2\"/>" +
                "<node n1:id=\"n1\" n2:id=\"n2\">content</node>" +
                "</root>";
        testControllers("n1,n2,content:node1,content1:node2", xml, MultipleNamespacesProcessor.class);
    }

    @Namespaces(
            value = {
                    @Namespace(value = "rootNamespace", shorthand = "r"),
                    @Namespace(value = "namespace1", shorthand = "ns1"),
                    @Namespace(value = "namespace2", shorthand = "ns2")},
            defaultShorthand = "r"
    )
    public static class MultipleNamespacesProcessor {

        @Node("root")
        public String root(@Source("node") String node,
                           @Source(ns = "ns1", value = "node") String node1,
                           @Source(ns = "ns2", value = "node") String node2) {
            return node + ":" + node1 + ":" + node2;
        }

        @Node("node")
        public String node(@Attribute(ns = "ns1", value = "id") String n1Id,
                           @Attribute(ns = "ns2", value = "id") String n2Id,
                           @Node("node") String content) {
            return n1Id + "," + n2Id + "," + content;
        }

        @Node(ns = "ns1", value = "node")
        public String node1(@Attribute("id") String id, @Node("content") String content) {
            return id + "," + content;
        }

        @Node(ns = "ns2", value = "node")
        public String node2(@Attribute("id") String id) {
            return id;
        }
    }
}
