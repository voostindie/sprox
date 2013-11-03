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

import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MovieTitleFinderTest {

    @Test
    public void testFindTitleInNodeBodyForFirstMovie() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerObject(new MovieTitleFinder())
                .buildXmlProcessor();
        final String title = processor.execute(getClass().getResourceAsStream("/movies.xml"));
        assertThat(title, is("The Shawshank Redemption"));
    }

    public static final class MovieTitleFinder {
        private boolean first = true;
        @Node("movie")
        public String findFirstMovie(@Node("title") String title) {
            if (first) {
                first = false;
                return title;
            } else {
                return null;
            }
        }
    }
}
