package nl.ulso.sprox.opml;

public interface Visitable {

    void accept(Visitor visitor);
}
