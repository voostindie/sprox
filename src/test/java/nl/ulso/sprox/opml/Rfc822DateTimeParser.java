package nl.ulso.sprox.opml;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static org.joda.time.format.DateTimeFormat.forPattern;

public class Rfc822DateTimeParser implements Parser<DateTime> {

    // Not sure if this pattern is completely correct, but all dates in the test data are parsed correctly.
    private static final DateTimeFormatter parser = forPattern("EEE, dd MMM YYYY HH:mm:ss ZZZ");

    public DateTime fromString(String value) throws ParseException {
        try {
            return parser.parseDateTime(value);
        } catch (IllegalArgumentException e) {
            throw new ParseException(DateTime.class, value, e);
        }
    }
}
