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

package nl.ulso.sprox.movies;

import nl.ulso.sprox.ControllerFactory;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import java.io.InputStream;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MovieCounterTest {

    @Test
    public void testCountAllMovies() throws Exception {
        final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
                .addControllerClass(MovieCounter.class)
                .buildXmlProcessor();
        final Integer count = processor.execute(getMoviesResource());
        assertThat(count, is(5));
    }


    @Test
    public void testCountAllMoviesUsingControllerFactoryFromInnerClass() throws Exception {
        //noinspection Convert2Lambda,Anonymous2MethodRef
        final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
                .addControllerFactory(new ControllerFactory<MovieCounter>() {
                    @Override
                    public MovieCounter createController() {
                        return new MovieCounter();
                    }
                }).buildXmlProcessor();
        final Integer count = processor.execute(getMoviesResource());
        assertThat(count, is(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountAllMoviesUsingControllerFactoryFromMethodReferenceWithoutTypeInformation() throws Exception {
        //noinspection Convert2Lambda
        createXmlProcessorBuilder(Integer.class).addControllerFactory(MovieCounter::new);
    }

    @Test
    public void testCountAllMoviesUsingControllerFactoryFromMethodReferenceWithTypeInformation() throws Exception {
        final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
                .addControllerFactory(MovieCounter::new, MovieCounter.class).buildXmlProcessor();
        final Integer count = processor.execute(getMoviesResource());
        assertThat(count, is(5));
    }

    @Test
    public void testCountAllStars() throws Exception {
        final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
                .addControllerClass(StarCounter.class)
                .buildXmlProcessor();
        final Integer count = processor.execute(getMoviesResource());
        assertThat(count, is(3));
    }

    @Test
    public void testCountAllMoviesAndStars() throws Exception {
        final MovieCounter movieCounter = new MovieCounter();
        final StarCounter starCounter = new StarCounter();
        final XmlProcessor<Void> processor = createXmlProcessorBuilder(Void.class)
                .addControllerObject(movieCounter)
                .addControllerObject(starCounter)
                .buildXmlProcessor();
        processor.execute(getMoviesResource());
        assertThat(movieCounter.getCount(), is(5));
        assertThat(starCounter.getCount(), is(3));
    }

    private InputStream getMoviesResource() {
        return getClass().getResourceAsStream("/movies.xml");
    }

    public static final class MovieCounter {
        private int count;

        @Node("movies")
        public Integer getCount() {
            return count;
        }

        @Node("movie")
        public void incrementCounter() {
            count++;
        }
    }

    public static final class StarCounter {
        private int count;

        @Node("stars")
        public Integer getCount() {
            return count;
        }

        @Node("star")
        public void incrementCounter() {
            count++;
        }
    }
}
