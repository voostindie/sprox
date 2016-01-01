package nl.ulso.sprox.opml;

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Recursive;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

public class OutlineFactory {

    @Node
    public Outline opml(@Node String title, @Node DateTime dateCreated,
                        @Node DateTime dateModified, List<Element> elements) {
        return new Outline(title, dateCreated, dateModified, elements);
    }

    @Recursive
    @Node
    public Element outline(@Attribute String text, Optional<List<Element>> elements) {
        return new Element(text, elements);
    }
}
