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
