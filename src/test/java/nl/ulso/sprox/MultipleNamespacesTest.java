package nl.ulso.sprox;

import org.junit.Test;

import static nl.ulso.sprox.SproxTests.testControllers;

public class MultipleNamespacesTest {

    public static final String XML = "<root xmlns=\"rootNamespace\" xmlns:n1=\"namespace1\" xmlns:n2=\"namespace2\">" +
            "<n1:node id=\"node1\"><n1:content>content1</n1:content></n1:node>" +
            "<n2:node id=\"node2\"/>" +
            "<node n1:id=\"n1\" n2:id=\"n2\">content</node>" +
            "</root>";

    @Test
    public void testThatProcessingXmlWithMultipleNamespacesWorksCorrectly() throws Exception {
        testControllers("n1,n2,content:node1,content1:node2", XML, MultipleNamespacesProcessor.class);
    }

    @Test
    public void testThatProcessingXmlWithMultipleNamespacesWorksCorrectlyUsingJdk8Repeatable() throws Exception {
        testControllers("n1,n2,content:node1,content1:node2", XML, MultipleNamespacesProcessorWithRepeatable.class);
    }

    @Namespaces({
            @Namespace(value = "rootNamespace"),
            @Namespace(value = "namespace1", shorthand = "ns1"),
            @Namespace(value = "namespace2", shorthand = "ns2")
    })
    public static class MultipleNamespacesProcessor {

        @Node("root")
        public String root(@Source("node") String node, @Source("ns1:node") String node1,
                           @Source("ns2:node") String node2) {
            return node + ":" + node1 + ":" + node2;
        }

        @Node("node")
        public String node(@Attribute("ns1:id") String n1Id, @Attribute("ns2:id") String n2Id,
                           @Node("node") String content) {
            return n1Id + "," + n2Id + "," + content;
        }

        @Node("ns1:node")
        public String node1(@Attribute("id") String id, @Node("content") String content) {
            return id + "," + content;
        }

        @Node("ns2:node")
        public String node2(@Attribute("id") String id) {
            return id;
        }
    }

    @Namespace(value = "rootNamespace")
    @Namespace(value = "namespace1", shorthand = "ns1")
    @Namespace(value = "namespace2", shorthand = "ns2")
    public static class MultipleNamespacesProcessorWithRepeatable {

        @Node("root")
        public String root(@Source("node") String node, @Source("ns1:node") String node1,
                           @Source("ns2:node") String node2) {
            return node + ":" + node1 + ":" + node2;
        }

        @Node("node")
        public String node(@Attribute("ns1:id") String n1Id, @Attribute("ns2:id") String n2Id,
                           @Node("node") String content) {
            return n1Id + "," + n2Id + "," + content;
        }

        @Node("ns1:node")
        public String node1(@Attribute("id") String id, @Node("content") String content) {
            return id + "," + content;
        }

        @Node("ns2:node")
        public String node2(@Attribute("id") String id) {
            return id;
        }
    }
}
