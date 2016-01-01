package nl.ulso.sprox.parsers;

import nl.ulso.sprox.Parser;
import nl.ulso.sprox.ParseException;

/**
 * Simple no-op parser that returns the string value itself.
 * <p>
 * Reasons for having this implementation:
 * </p>
 * <ul>
 * <li>It makes the implementation of the processor easier. Strings are not a special case.</li>
 * <li>It allows developers to replace this parser with their own.</li>
 * </ul>
 */
public class StringParser implements Parser<String> {
    @Override
    public String fromString(String value) throws ParseException {
        return value;
    }
}
