package nl.ulso.sprox.atom;

import nl.ulso.sprox.ParseException;
import nl.ulso.sprox.Parser;

import static nl.ulso.sprox.atom.TextType.*;

public class TextTypeParser implements Parser<TextType> {
    @Override
    public TextType fromString(String value) throws ParseException {
        switch (value.toLowerCase()) {
            case "text":
                return TEXT;
            case "html":
                return HTML;
            case "xhtml":
                return XHTML;
            default:
                throw new ParseException(TextType.class, value);
        }
    }
}
