package nl.ulso.sprox;

import org.junit.Test;

import static nl.ulso.sprox.SproxTests.testControllers;

public class NodeWithAttributesAndContentTest {

    @Test
    public void testControllerOnNodeThatPullsContentFromThatSameNode() throws Exception {
        testControllers(
                "id:content",
                "<root id=\"id\">content</root>",
                new NodeWithAttributesAndContentProcessor());
    }

    public static final class NodeWithAttributesAndContentProcessor {
        @Node("root")
        public String joinAttributeAndContent(@Attribute("id") String id, @Node("root") String content) {
            return id + ":" + content;
        }
    }
}
