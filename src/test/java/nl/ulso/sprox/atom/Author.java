package nl.ulso.sprox.atom;

public class Author {
    private final String name;
    private final String uri;
    private final String email;
    private final Image image;

    public Author(String name, String uri, String email, Image image) {
        this.name = name;
        this.uri = uri;
        this.email = email;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getEmail() {
        return email;
    }

    public Image getImage() {
        return image;
    }
}
