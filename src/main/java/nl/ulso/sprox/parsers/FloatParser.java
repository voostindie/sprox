package nl.ulso.sprox.parsers;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

public class FloatParser implements Parser<Float> {
    @Override
    public Float fromString(String value) throws ParseException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ParseException(Float.class, value, e);
        }
    }
}
