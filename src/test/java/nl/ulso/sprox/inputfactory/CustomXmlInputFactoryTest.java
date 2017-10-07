package nl.ulso.sprox.inputfactory;

import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory;
import nl.ulso.sprox.opml.Outline;
import nl.ulso.sprox.opml.OutlineFactory;
import nl.ulso.sprox.opml.Rfc822DateTimeParser;
import org.junit.Test;

import javax.xml.stream.*;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import java.io.InputStream;
import java.io.Reader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CustomXmlInputFactoryTest {

    @Test
    public void testCustomXmlInputFactory() throws Exception {
        final StringBuilder builder = new StringBuilder();
        final XMLInputFactory factory = createXmlInputFactory(builder);
        assertNotNull(factory);
        final XmlProcessor<Outline> processor = new StaxBasedXmlProcessorBuilderFactory()
                .createXmlProcessorBuilder(Outline.class)
                .addControllerClass(OutlineFactory.class)
                .addParser(new Rfc822DateTimeParser())
                .setXmlInputFactory(factory)
                .buildXmlProcessor();
        final Outline outline = processor.execute(getClass().getResourceAsStream("/states.opml"));
        assertNotNull(outline);
        assertThat(builder.toString(), equalTo("Creating custom XML Event Reader"));
    }

    private XMLInputFactory createXmlInputFactory(final StringBuilder builder) {
        return new XMLInputFactory() {

            private final XMLInputFactory wrappedFactory = XMLInputFactory.newDefaultFactory();

            public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
                builder.append("Creating custom XML Event Reader");
                return wrappedFactory.createXMLEventReader(stream);
            }

            @Override
            public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
                return null;
            }

            @Override
            public XMLResolver getXMLResolver() {
                return null;
            }

            @Override
            public void setXMLResolver(XMLResolver resolver) {

            }

            @Override
            public XMLReporter getXMLReporter() {
                return null;
            }

            @Override
            public void setXMLReporter(XMLReporter reporter) {

            }

            @Override
            public void setProperty(String name, Object value) throws IllegalArgumentException {

            }

            @Override
            public Object getProperty(String name) throws IllegalArgumentException {
                return null;
            }

            @Override
            public boolean isPropertySupported(String name) {
                return false;
            }

            @Override
            public void setEventAllocator(XMLEventAllocator allocator) {

            }

            @Override
            public XMLEventAllocator getEventAllocator() {
                return null;
            }
        };
    }
}
