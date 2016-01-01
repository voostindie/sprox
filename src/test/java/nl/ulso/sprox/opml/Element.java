package nl.ulso.sprox.opml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class Element implements Iterable<Element>, Visitable {
    private final String text;
    private final List<Element> elements;

    public Element(String text, Optional<List<Element>> elements) {
        this.text = text;
        this.elements = elements
                .map(list -> unmodifiableList(new ArrayList<>(list)))
                .orElse(emptyList());
    }

    public String getText() {
        return text;
    }

    public int getNumberOfElements() {
        return elements.size();
    }

    public Element getElementAt(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        for (Element element : elements) {
            element.accept(visitor);
        }
    }
}
