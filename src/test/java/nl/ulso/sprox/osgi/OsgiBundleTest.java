package nl.ulso.sprox.osgi;

import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.XmlProcessorBuilderFactory;
import nl.ulso.sprox.opml.OutlineFactoryTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.io.StringReader;

import static java.lang.Class.forName;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Tests Sprox as an OSGi bundle. This test is kind of special, because normally OSGi integration tests are implemented
 * in separate test modules. In this case, it's all in one module. That requires some special configuration in the
 * Maven project (see {@code pom.xml} as well as here.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OsgiBundleTest {

    @Inject
    private XmlProcessorBuilderFactory factory;

    @Configuration
    public Option[] config() {
        return options(
                bundle("reference:file:target/classes"), // Add the compiler output as a bundle
                mavenBundle("joda-time", "joda-time", "2.3"),
                junitBundles()
        );
    }

    @Test
    public void testXmlProcessorBuilderFactory() throws Exception {
        assertNotNull(factory);
    }

    @Test
    public void testNodeCounterProcessor() throws Exception {
        final XmlProcessor<Integer> processor = factory.createXmlProcessorBuilder(Integer.class)
                .addControllerClass(NodeCounter.class)
                .buildXmlProcessor();
        final int count = processor.execute(new StringReader("<root><node/><node/><node/></root>"));
        assertThat(count, is(3));
    }

    @Test
    public void testOpmlInOsgiEnvironment() throws Exception {
        final OutlineFactoryTest test = new OutlineFactoryTest();
        test.setFactory(factory);
        test.testOutlineFactory();
    }

    @Test
    public void testInterfaceClassesAreAvailable() throws Exception {
        forName("nl.ulso.sprox.XmlProcessorBuilderFactory");
    }

    @Test(expected = ClassNotFoundException.class)
    public void testImplementationClassesAreUnavailable() throws Exception {
        forName("nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory");
    }

    @Test
    public void testParsersAreAvailable() throws Exception {
        forName("nl.ulso.sprox.parsers.BooleanParser");
        forName("nl.ulso.sprox.parsers.ByteParser");
        forName("nl.ulso.sprox.parsers.CharacterParser");
        forName("nl.ulso.sprox.parsers.DoubleParser");
        forName("nl.ulso.sprox.parsers.FloatParser");
        forName("nl.ulso.sprox.parsers.IntegerParser");
        forName("nl.ulso.sprox.parsers.LongParser");
        forName("nl.ulso.sprox.parsers.ShortParser");
        forName("nl.ulso.sprox.parsers.StringParser");
    }

    @Test
    public void testResolversAreAvailable() throws Exception {
        forName("nl.ulso.sprox.resolvers.DefaultElementNameResolver");
        forName("nl.ulso.sprox.resolvers.CamelCaseToHyphensElementNameResolver");
    }

    public static class NodeCounter {
        private int count = 0;

        @Node("root")
        public Integer totalCount() {
            return count;
        }

        @Node("node")
        public void countNode() {
            count++;
        }
    }
}
