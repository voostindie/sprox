package nl.ulso.sprox;

/**
 * Exception thrown when parsing a string into a specific type failed.
 */
public final class ParseException extends XmlProcessorException {
    public ParseException(Class resultClass, String value) {
        super(createMessage(resultClass, value));
    }

    public ParseException(Class resultClass, String value, Throwable cause) {
        super(createMessage(resultClass, value), cause);
    }

    private static String createMessage(Class resultClass, String value) {
        return "Could not parse string \"" + value + "\" into a value of type \"" + resultClass + "\"";
    }
}
