package nl.ulso.sprox;

import nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory;

import java.io.StringReader;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

/**
 * Utility methods for testing Sprox
 */
public final class SproxTests {

    private SproxTests() {
    }

    public static <T> XmlProcessorBuilder<T> createXmlProcessorBuilder(Class<T> resultClass) {
        return new StaxBasedXmlProcessorBuilderFactory().createXmlProcessorBuilder(requireNonNull(resultClass));
    }

    public static <T> void testProcessor(T expected, String xml, XmlProcessor<T> processor) throws Exception {
        final T actual = processor.execute(new StringReader(xml));
        assertEquals(expected, actual);
    }

    public static <T> void testControllers(T expected, String xml, Object... controllers) throws Exception {
        @SuppressWarnings("unchecked")
        final XmlProcessorBuilder<Object> builder = (XmlProcessorBuilder<Object>) createXmlProcessorBuilder(expected.getClass());
        for (Object controller : controllers) {
            if (controller instanceof Class) {
                builder.addControllerClass((Class) controller);
            } else if (controller instanceof ControllerFactory) {
                builder.addControllerFactory((ControllerFactory<?>) controller);
            } else {
                builder.addControllerObject(controller);
            }
        }
        final XmlProcessor<Object> processor = builder.buildXmlProcessor();
        testProcessor(expected, xml, processor);
    }
}
