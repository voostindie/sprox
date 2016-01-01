package nl.ulso.sprox.serviceloader;

import nl.ulso.sprox.XmlProcessorBuilderFactory;
import nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory;
import org.junit.Test;

import java.util.Iterator;
import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the {@link XmlProcessorBuilderFactory} is registered in the JDK's ServiceLoader.
 */
public class ServiceLoaderTest {
    @Test
    public void testServiceLoaderMechanism() throws Exception {
        load(XmlProcessorBuilderFactory.class).iterator().next();
        final Iterator<XmlProcessorBuilderFactory> iterator = load(XmlProcessorBuilderFactory.class).iterator();
        assertTrue(iterator.hasNext());
        final XmlProcessorBuilderFactory factory = iterator.next();
        assertNotNull(factory);
        assertTrue(factory instanceof StaxBasedXmlProcessorBuilderFactory);

    }
}
