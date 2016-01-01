package nl.ulso.sprox.parsers;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

public class ByteParser implements Parser<Byte> {
    @Override
    public Byte fromString(String value) throws ParseException {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            throw new ParseException(Byte.class, value, e);
        }
    }
}
