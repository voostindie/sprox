package nl.ulso.sprox.parsers;

import nl.ulso.sprox.Parser;
import nl.ulso.sprox.ParseException;

public class DoubleParser implements Parser<Double> {
    @Override
    public Double fromString(String value) throws ParseException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParseException(Double.class, value, e);
        }
    }
}
