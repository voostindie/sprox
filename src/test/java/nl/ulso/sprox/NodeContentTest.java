/*
 * Copyright 2013 Vincent Oostindië
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

package nl.ulso.sprox;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static nl.ulso.sprox.SproxTests.testControllers;
import static nl.ulso.sprox.SproxTests.testProcessor;
import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;

public class NodeContentTest {

    @Test
    public void testThatContentFromFirstNestedNodeIsReturned() throws Exception {
        testControllers(
                "value1",
                "<root><node>value1</node><node>value3</node></root>",
                new NestedNodeContentProcessor()
        );
    }

    @Test
    public void testThatContentFromNearestNodeIsReturned() throws Exception {
        testControllers(
                "value3",
                "<root><subnode><node>value2</node></subnode><node>value3</node></root>",
                new NestedNodeContentProcessor()
        );
    }

    @Test
    public void testThatNestedNodeContentIsIgnored() throws Exception {
        testControllers(
                "",
                "<root><node><subnode>value1</subnode></node></root>",
                new NestedNodeContentProcessor());
    }

    @Test
    public void testMappingForPrimitiveBooleanInContent() throws Exception {
        testControllers(true, "<root><boolean>true</boolean></root>", new BooleanProcessor());
    }

    @Test
    public void testMappingForCustomTypeInContent() throws Exception {
        final XmlProcessor<Date> processor = createXmlProcessorBuilder(Date.class)
                .addControllerObject(new DateProcessor())
                .addParser(new DateParser())
                .buildXmlProcessor();
        final Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2012-12-21");
        testProcessor(date, "<root><date>2012-12-21</date></root>", processor);
    }

    @Test
    public void testOuterNodeWithContent() throws Exception {
        testControllers("outer", "<outer>outer</outer>", InnerAndOuterNodeContentProcessor.class);
    }

    @Test
    public void testInnerNodeWithContent() throws Exception {
        testControllers("inner", "<outer><inner>inner</inner></outer>", InnerAndOuterNodeContentProcessor.class);
    }

    @Test
    public void testNodeContentWithIdenticalNames1() throws Exception {
        final Element structure = new Element("outer", new Element("inner", null));
        testControllers(structure, "<outer><title>outer</title><inner><title>inner</title></inner></outer>", new ElementProcessor());
    }

    @Test
    public void testNodeContentWithIdenticalNames2() throws Exception {
        final Element structure = new Element("outer", new Element("inner", null));
        testControllers(structure, "<outer><inner><title>inner</title></inner><title>outer</title></outer>", new ElementProcessor());
    }

    public static final class NestedNodeContentProcessor {
        @Node("root")
        public String getNestedContent(@Nullable @Node("node") String content) {
            return content != null ? content : "";
        }
    }

    public static final class BooleanProcessor {
        @Node("root")
        public Boolean getBoolean(@Node("boolean") boolean value) {
            return value;
        }
    }

    public static final class DateParser implements Parser<Date> {
        @Override
        public Date fromString(String value) throws ParseException {
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return format.parse(value);
            } catch (java.text.ParseException e) {
                throw new ParseException(Date.class, value);
            }
        }
    }

    public static final class DateProcessor {
        @Node("root")
        public Date getDate(@Node("date") Date date) {
            return date;
        }
    }

    public static final class InnerAndOuterNodeContentProcessor {
        @Node("outer")
        public String getContent(@Nullable @Node("outer") String outer, @Nullable @Node("inner") String inner) {
            return outer != null ? outer : inner;
        }
    }

    public static final class Element {
        private final String title;
        private final Element child;

        public Element(String title, Element child) {
            this.title = title;
            this.child = child;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Element element = (Element) o;
            if (child != null ? !child.equals(element.child) : element.child != null) return false;
            if (!title.equals(element.title)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = title.hashCode();
            result = 31 * result + (child != null ? child.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "title='" + title + '\'' +
                    ", child=" + child +
                    '}';
        }
    }

    public static final class ElementProcessor {
        @Node("outer")
        public Element outer(@Node("title") String title, Element child) {
            return new Element(title, child);
        }

        @Node("inner")
        public Element inner(@Node("title") String title) {
            return new Element(title, null);
        }
    }
}
