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

package nl.ulso.sprox.movies;

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import java.util.Optional;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MovieIdFinderTest {

    @Test
    public void testFindIdInAttributeOfFirstMovie() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerObject(new MovieIdFinder())
                .buildXmlProcessor();
        final String id = processor.execute(getClass().getResourceAsStream("/movies.xml"));
        assertThat(id, is("tt0111161"));
    }

    @Test
    public void testCountAllByInvalidRequiredAttribute() throws Exception {
        final MovieCounterByInvalidRequiredAttribute counter = new MovieCounterByInvalidRequiredAttribute();
        final XmlProcessor<Void> processor = createXmlProcessorBuilder(Void.class)
                .addControllerObject(counter)
                .buildXmlProcessor();
        processor.execute(getClass().getResourceAsStream("/movies.xml"));
        assertThat(counter.count, is(0));
    }

    @Test
    public void testCountAllByInvalidOptionalAttribute() throws Exception {
        final MovieCounterByInvalidOptionalAttribute counter = new MovieCounterByInvalidOptionalAttribute();
        final XmlProcessor<Void> processor = createXmlProcessorBuilder(Void.class)
                .addControllerObject(counter)
                .buildXmlProcessor();
        processor.execute(getClass().getResourceAsStream("/movies.xml"));
        assertThat(counter.count, is(5));
    }

    public static final class MovieIdFinder {
        private boolean first = true;

        @Node("movie")
        public String findFirstMovie(@Attribute("id") String id) {
            if (first) {
                first = false;
                return id;
            } else {
                return null;
            }
        }
    }

    public static final class MovieCounterByInvalidRequiredAttribute {
        int count;

        @Node("movie")
        public void findFirstMovie(@Attribute("invalid") String id) {
            count++;
        }
    }

    public static final class MovieCounterByInvalidOptionalAttribute {

        int count;

        @Node("movie")
        public void findFirstMovie(@Attribute("invalid") Optional<String> id) {
            count++;
        }
    }
}
