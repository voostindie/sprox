package nl.ulso.sprox.opml;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;

public class Outline extends Element {
    private final DateTime creationDate;
    private final DateTime modificationDate;

    public Outline(String title, DateTime creationDate, DateTime modificationDate, List<Element> elements) {
        super(title, of(elements));
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    public String getTitle() {
        return getText();
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        for (Element element : this) {
            element.accept(visitor);
        }
    }
}
