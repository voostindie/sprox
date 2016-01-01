package nl.ulso.sprox;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * The result of a parser might be {@code null}. The result should be an optional.
 */
public class NullValueParserTest {

    @Test
    public void testParseNodeContentIntoNull() throws Exception {
        final StringBuilder parsedValues = new StringBuilder();
        final XmlProcessor<String> processor = SproxTests.createXmlProcessorBuilder(String.class)
                .addParser((String s) -> {
                    parsedValues.append(s);
                    return null;
                }, String.class)
                .addControllerClass(NullNodeContentProcessor.class)
                .buildXmlProcessor();

        SproxTests.testProcessor("NULL", "<root>CONTENT</root>", processor);
        assertThat(parsedValues.toString(), is("CONTENT"));
    }

    @Test
    public void testParseNodeAttributeIntoNull() throws Exception {
        final StringBuilder parsedValues = new StringBuilder();
        final XmlProcessor<String> processor = SproxTests.createXmlProcessorBuilder(String.class)
                .addParser((String s) -> {
                    parsedValues.append(s);
                    return null;
                }, String.class)
                .addControllerClass(NullNodeAttributeProcessor.class)
                .buildXmlProcessor();

        SproxTests.testProcessor("NULL", "<root attribute=\"ATTRIBUTE\"/>", processor);
        assertThat(parsedValues.toString(), is("ATTRIBUTE"));
    }


    public static class NullNodeContentProcessor {
        @Node
        public String root(@Node Optional<String> root) {
            return root.orElse("NULL");
        }
    }

    public static class NullNodeAttributeProcessor {
        @Node
        public String root(@Attribute Optional<String> attribute) {
            return attribute.orElse("NULL");
        }
    }
}
