package nl.ulso.sprox.parsers;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

public class ShortParser implements Parser<Short> {
    @Override
    public Short fromString(String value) throws ParseException {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            throw new ParseException(Short.class, value, e);
        }
    }
}
