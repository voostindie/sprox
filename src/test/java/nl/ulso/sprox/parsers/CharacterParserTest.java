package nl.ulso.sprox.parsers;

import nl.ulso.sprox.ParseException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class CharacterParserTest {

    private CharacterParser parser = new CharacterParser();

    @Test
    public void testParseSingleCharacter() throws Exception {
        final Character result = parser.fromString("v");
        assertThat(result.charValue(), is('v'));
    }

    @Test(expected = ParseException.class)
    public void testParseZeroCharacters() throws Exception {
        parser.fromString("");
    }

    @Test(expected = ParseException.class)
    public void testParseMultipleCharacters() throws Exception {
        parser.fromString("foo");
    }
}