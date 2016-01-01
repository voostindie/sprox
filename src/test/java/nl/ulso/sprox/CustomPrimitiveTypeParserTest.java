package nl.ulso.sprox;

import org.junit.Test;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static nl.ulso.sprox.SproxTests.testProcessor;

public class CustomPrimitiveTypeParserTest {
    @Test
    public void testCustomIntegerParserFromInnerClass() throws Exception {
        //noinspection Convert2Lambda
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerClass(IntegerParser.class)
                .addParser(new Parser<Integer>() {
                    @Override
                    public Integer fromString(String value) throws ParseException {
                        return abs(parseInt(value));
                    }
                })
                .buildXmlProcessor();
        testProcessor("42", "<number value=\"-42\"/>", processor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomIntegerParserFromLambdaWithoutTypeInformation() throws Exception {
        createXmlProcessorBuilder(String.class).addParser(value -> abs(parseInt(value)));
    }

    @Test
    public void testCustomIntegerParserFromLambdaWithTypeInformation() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerClass(IntegerParser.class)
                .addParser(value -> abs(parseInt(value)), Integer.class)
                .buildXmlProcessor();
        testProcessor("42", "<number value=\"-42\"/>", processor);
    }

    public static final class IntegerParser {
        @Node("number")
        public String getInteger(@Attribute("value") Integer value) {
            return value.toString();
        }
    }
}
