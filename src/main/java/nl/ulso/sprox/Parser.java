package nl.ulso.sprox;

/**
 * Parses a string from a node or an attribute into a specific type.
 * <p>
 * Sprox has built-in parsers for all primitive types. By implementing this interface and registering it through the
 * {@link XmlProcessorBuilder} you can add your own, or override the default ones.
 * </p>
 * <p>
 * When implementing a parser, make sure it is thread-safe. {@link XmlProcessor}s can be be used concurrently.
 * </p>
 */
public interface Parser<T> {

    /**
     * Parses a string value into a specific type.
     *
     * @param value String value to parse.
     * @return The result of parsing the value.
     * @throws ParseException If the value could not be parsed.
     */
    T fromString(String value) throws ParseException;
}
