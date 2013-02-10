package nl.ulso.sprox;

import org.junit.Test;

import static nl.ulso.sprox.SproxTests.testControllers;
import static nl.ulso.sprox.XmlProcessorFactory.createXmlProcessorBuilder;

public class SingleNamespaceTest {
    @Test
    public void testSingleNamespace() throws Exception {
        testControllers(
                "42:answer",
                "<root xmlns=\"namespace\" id=\"42\"><node>answer</node></root>",
                new SingleNamespaceProcessor());

    }

    @Test(expected = IllegalStateException.class)
    public void testThatNamespacesAreGlobalForAProcessor() throws Exception {
        createXmlProcessorBuilder(Void.class)
                .addControllerClass(SingleNamespaceProcessor.class)
                .addControllerClass(NoNamespaceProcessor.class)
                .buildXmlProcessor();
    }

    @Namespace("namespace")
    public static final class SingleNamespaceProcessor {
        @Node("root")
        public String root(@Attribute("id") String id, @Node("node") String content) {
            return id + ":" + content;
        }
    }

    public static final class NoNamespaceProcessor {
        @Node("root")
        public String root(@Attribute("id") String id, @Node("node") String content) {
            return id + ":" + content;
        }
    }
}
