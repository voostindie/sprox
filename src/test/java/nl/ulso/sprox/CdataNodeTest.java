package nl.ulso.sprox;

import org.junit.Test;

import static nl.ulso.sprox.SproxTests.testControllers;

/**
 *
 */
public class CdataNodeTest {

    @Test
    public void testThatCdataIsReadAsContent() throws Exception {
        testControllers("content", "<root><![CDATA[content]]></root>", new NodeProcessor());
    }

    public static final class NodeProcessor {
        @Node("root")
        public String root(@Node("root") String content) {
            return content;
        }
    }
}
