package nl.ulso.sprox;

import org.junit.Test;

import static nl.ulso.sprox.SproxTests.testProcessor;
import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;

public class ExceptionalSituationsTest {

    @Test(expected = IllegalStateException.class)
    public void testThatCreatingAProcessorWithZeroControllersFails() throws Exception {
        createXmlProcessorBuilder(Void.class).buildXmlProcessor();
    }

    @Test(expected = IllegalStateException.class)
    public void testThatCreatingAProcessorWithControllersWithoutAnnotationsFails() throws Exception {
        createXmlProcessorBuilder(Void.class).addControllerObject("").buildXmlProcessor();
    }

    @Test(expected = XmlProcessorException.class)
    public void testThatParseExceptionResultsInFailure() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerObject(new BrokenNodeProcessor("test"))
                .addParser(value -> {
                    throw new ParseException(String.class, value);
                }, String.class).buildXmlProcessor();
        testProcessor("", "<root><node>value</node></root>", processor);
    }

    @Test(expected = IllegalStateException.class)
    public void testThatClassInstantiationOnAControllerWithoutNoArgConstructorFails() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerClass(BrokenNodeProcessor.class)
                .buildXmlProcessor();
        testProcessor("", "<root/>", processor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatAControllerClassCanBeRegisteredOnlyOnce() throws Exception {
        createXmlProcessorBuilder(String.class)
                .addControllerClass(CheckedExceptionThrowingProcessor.class)
                .addControllerClass(CheckedExceptionThrowingProcessor.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testThatBothNamespaceAndNamespacesAnnotationIsInvalid() throws Exception {
        createXmlProcessorBuilder(Void.class)
                .addControllerClass(DuplicateNamespaceDeclaration.class)
                .buildXmlProcessor();

    }

    @Test(expected = XmlProcessorException.class)
    public void testThatNonVoidProcessorReturningNullIsInvalid() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerObject(new BrokenNodeProcessor("test"))
                .buildXmlProcessor();
        testProcessor("", "<non-match/>", processor);
    }

    @Test(expected = XmlProcessorException.class)
    public void testThatCheckedExceptionThrownFromControllerResultsInXmlProcessorException() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerObject(new CheckedExceptionThrowingProcessor())
                .buildXmlProcessor();
        testProcessor("", "<exception/>", processor);
    }

    @Test(expected = NullPointerException.class)
    public void testThatUncheckedExceptionThrowFromControllerResultsInThatSameException() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerObject(new UncheckedExceptionThrowingProcessor())
                .buildXmlProcessor();
        testProcessor("", "<exception/>", processor);
    }

    public static final class BrokenNodeProcessor {

        private final String name;

        public BrokenNodeProcessor(String name) {
            this.name = name;
        }

        @Node("root")
        public String root(String node) {
            return node;
        }

        @Node("node")
        public String node(@Node("node") String value) {
            return value;
        }
    }

    @Namespaces(value = {@Namespace(shorthand = "ns1", value = "namespace1")})
    @Namespace("namespace2")
    public static final class DuplicateNamespaceDeclaration {
        @Node("root")
        public void root() {
        }
    }

    public static final class CheckedExceptionThrowingProcessor {
        @Node("exception")
        public void exception() throws Exception {
            throw new Exception("error!");
        }
    }

    public static final class UncheckedExceptionThrowingProcessor {
        @Node("exception")
        public void exception() throws Exception {
            throw new NullPointerException("error!");
        }
    }
}
