package nl.ulso.sprox.atom;

public class HtmlText implements Text {
    private final String content;

    public HtmlText(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
