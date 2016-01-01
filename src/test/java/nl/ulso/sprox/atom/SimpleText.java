package nl.ulso.sprox.atom;

public class SimpleText implements Text {
    private final String content;

    public SimpleText(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
