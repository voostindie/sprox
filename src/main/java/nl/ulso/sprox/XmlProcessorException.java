package nl.ulso.sprox;

/**
 * Exception thrown whenever an error occurred while processing XML.
 */
public class XmlProcessorException extends Exception {
    public XmlProcessorException(Throwable cause) {
        super(cause);
    }

    public XmlProcessorException(String message) {
        super(message);
    }

    public XmlProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
