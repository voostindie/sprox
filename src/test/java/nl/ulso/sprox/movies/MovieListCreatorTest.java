package nl.ulso.sprox.movies;

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class MovieListCreatorTest {

    @Test
    public void testCreateMovieList() throws Exception {
        final XmlProcessor<MovieCollection> processor = createXmlProcessorBuilder(MovieCollection.class)
                .addControllerObject(new MovieListCreator())
                .buildXmlProcessor();
        final MovieCollection collection = processor.execute(getClass().getResourceAsStream("/movies.xml"));
        assertNotNull(collection);
        assertThat(collection.size(), is(5));
        assertThat(collection.get(3).title, is("Pulp Fiction"));
        assertThat(collection.get(0).stars.size(), is(3));
        assertThat(collection.get(0).stars.get(0).id, is("nm0000209"));
        assertThat(collection.get(0).stars.get(0).name, is("Tim Robbins"));
    }

    public static final class MovieCollection {

        private final List<Movie> movies;

        public MovieCollection(List<Movie> movies) {
            this.movies = movies;
        }

        public int size() {
            return movies.size();
        }

        public Movie get(int index) {
            return movies.get(index);
        }
    }

    public static final class Movie {
        final String id;
        final String title;
        final double rating;
        final int votes;
        final List<Star> stars;

        public Movie(String id, String title, double rating, int votes, List<Star> stars) {
            this.id = id;
            this.title = title;
            this.rating = rating;
            this.votes = votes;
            this.stars = stars;
        }

        @Override
        public String toString() {
            return "Movie: { id: " + id + ", title: " + title + ", rating: " + rating + ", votes: " + votes + "}";
        }
    }

    public static final class Star {
        final String id;
        final String name;

        public Star(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static final class MovieListCreator {
        @Node("movies")
        public MovieCollection createCollection(List<Movie> movies) {
            return new MovieCollection(movies);
        }

        @Node("movie")
        public Movie createMovie(@Attribute("id") String id, @Node("title") String title,
                                 @Node("rating") double rating, @Node("votes") Integer votes,
                                 Optional<List<Star>> stars) {
            return new Movie(id, title, rating, votes, stars.orElse(null));
        }

        @Node("star")
        public Star createStar(@Attribute("id") String id, @Node("name") String name) {
            return new Star(id, name);
        }
    }
}
