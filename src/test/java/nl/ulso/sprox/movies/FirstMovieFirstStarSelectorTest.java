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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FirstMovieFirstStarSelectorTest {

    @Test
    public void testInjectionOfSingleObjects() throws Exception {
        final XmlProcessor<Movie> processor = createXmlProcessorBuilder(Movie.class)
                .addControllerObject(new FirstMovieFirstStarSelector())
                .buildXmlProcessor();
        final Movie movie = processor.execute(getClass().getResourceAsStream("/movies.xml"));
        assertNotNull(movie);
        assertNotNull(movie.title);
        assertThat(movie.title, is("The Shawshank Redemption"));
        assertNotNull(movie.star);
        assertNotNull(movie.star.name);
        assertThat(movie.star.name, is("Tim Robbins"));
    }

    public static final class Movie {
        private final String title;
        private final Star star;

        public Movie(String title, Star star) {
            this.title = title;
            this.star = star;
        }
    }

    public static final class Star {
        private final String name;

        public Star(String name) {
            this.name = name;
        }
    }

    public static final class FirstMovieFirstStarSelector {

        boolean firstMovie = true;
        boolean firstStar = true;

        @Node("root")
        public Movie selectFirstMovie(Movie firstMovie) {
            return firstMovie;
        }

        @Node("movie")
        public Movie createMovie(@Node("title") String title, Star firstStar) {
            if (firstMovie) {
                firstMovie = false;
                return new Movie(title, firstStar);
            }
            return null;
        }

        @Node("star")
        public Star createStar(@Node("name") String name) {
            if (firstStar) {
                firstStar = false;
                return new Star(name);
            }
            return null;
        }
    }
}
