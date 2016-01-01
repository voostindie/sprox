package nl.ulso.sprox.parsers;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

public class IntegerParser implements Parser<Integer> {
    @Override
    public Integer fromString(String value) throws ParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParseException(Integer.class, value, e);
        }
    }
}
