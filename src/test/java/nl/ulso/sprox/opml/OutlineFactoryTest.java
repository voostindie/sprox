/*
 * Copyright 2013-2014 Vincent Oostindië
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.sprox.opml;

import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.XmlProcessorBuilderFactory;
import nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory;
import org.junit.Before;
import org.junit.Test;

import static nl.ulso.sprox.opml.OutlineFactoryTest.ElementCounter.countElements;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class OutlineFactoryTest {

    private XmlProcessorBuilderFactory factory;

    public void setFactory(XmlProcessorBuilderFactory factory) {
        this.factory = factory;
    }

    @Before
    public void setUp() throws Exception {
        factory = new StaxBasedXmlProcessorBuilderFactory();
    }

    @Test
    public void testOutlineFactory() throws Exception {
        final XmlProcessor<Outline> processor = factory.createXmlProcessorBuilder(Outline.class)
                .addControllerClass(OutlineFactory.class)
                .addParser(new Rfc822DateTimeParser())
                .buildXmlProcessor();
        final Outline outline = processor.execute(getClass().getResourceAsStream("/states.opml"));
        assertNotNull(outline);
        assertThat(outline.getTitle(), is("states.opml"));
        assertThat(countElements(outline), is(63));
        assertThat(outline.getNumberOfElements(), is(1));
        final Element unitedStates = outline.getElementAt(0);
        assertThat(unitedStates.getText(), is("United States"));
        assertThat(unitedStates.getNumberOfElements(), is(8));
    }

    static class ElementCounter implements Visitor {
        private int count = 0;

        private ElementCounter() {
        }

        @Override
        public void visit(Outline outline) {
        }

        @Override
        public void visit(Element element) {
            count++;
        }

        public static int countElements(Outline outline) {
            final ElementCounter counter = new ElementCounter();
            outline.accept(counter);
            return counter.count;
        }
    }
}
