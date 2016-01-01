package nl.ulso.sprox.parsers;

import nl.ulso.sprox.Parser;
import nl.ulso.sprox.ParseException;

public class CharacterParser implements Parser<Character> {
    @Override
    public Character fromString(String value) throws ParseException {
        if (value.length() != 1) {
            throw new ParseException(Character.class, value);
        }
        return value.charAt(0);
    }
}
