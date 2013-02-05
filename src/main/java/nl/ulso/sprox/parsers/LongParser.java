package nl.ulso.sprox.parsers;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

public class LongParser implements Parser<Long> {
    @Override
    public Long fromString(String value) throws ParseException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParseException(Long.class, value);
        }
    }
}
