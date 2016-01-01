package nl.ulso.sprox.parsers;

import nl.ulso.sprox.ParseException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class BooleanParserTest {

    private BooleanParser parser = new BooleanParser();

    @Test
    public void testOneIsTrue() throws Exception {
        final Boolean result = parser.fromString("1");
        assertThat(result, is(true));
    }

    @Test
    public void testTrueIsTrue() throws Exception {
        final Boolean result = parser.fromString("true");
        assertThat(result, is(true));
    }

    @Test
    public void testZeroIsFalse() throws Exception {
        final Boolean result = parser.fromString("0");
        assertThat(result, is(false));
    }

    @Test
    public void testFalseIsFalse() throws Exception {
        final Boolean result = parser.fromString("false");
        assertThat(result, is(false));
    }

    @Test(expected = ParseException.class)
    public void testExceptionForUnknownData() throws Exception {
        parser.fromString("foo");
    }
}