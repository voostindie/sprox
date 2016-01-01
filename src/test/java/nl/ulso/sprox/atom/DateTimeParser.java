package nl.ulso.sprox.atom;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;
import org.joda.time.DateTime;

import static org.joda.time.DateTime.parse;

public class DateTimeParser implements Parser<DateTime> {
    @Override
    public DateTime fromString(String value) throws ParseException {
        try {
            return parse(value);
        } catch (IllegalArgumentException e) {
            throw new ParseException(DateTime.class, value, e);
        }
    }
}
