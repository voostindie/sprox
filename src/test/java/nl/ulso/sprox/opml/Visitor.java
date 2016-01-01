package nl.ulso.sprox.opml;

public interface Visitor {

    void visit(Outline outline);

    void visit(Element element);
}
