package nl.ulso.sprox.parsers;

import nl.ulso.sprox.Parser;
import nl.ulso.sprox.ParseException;

/**
 * Parses an `xsd:boolean` into a Boolean.
 */
public class BooleanParser implements Parser<Boolean> {
    @Override
    public Boolean fromString(String value) throws ParseException {
        if ("true".equals(value) || "1".equals(value)) {
            return Boolean.TRUE;
        }
        if ("false".equals(value) || "0".equals(value)) {
            return Boolean.FALSE;
        }
        throw new ParseException(Boolean.class, value);
    }
}
