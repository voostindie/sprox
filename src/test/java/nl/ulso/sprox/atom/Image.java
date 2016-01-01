package nl.ulso.sprox.atom;

public class Image {
    private final String src;
    private final int width;
    private final int height;

    public Image(String src, int width, int height) {
        this.src = src;
        this.width = width;
        this.height = height;
    }

    public String getSrc() {
        return src;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
