package nl.ulso.sprox.atom;

import org.joda.time.DateTime;

import java.util.Optional;

public class Entry {
    private final String id;
    private final DateTime publicationDate;
    private final Text title;
    private final Text content;
    private final String etag;
    private final Optional<Author> author;

    public Entry(String id, DateTime publicationDate, Text title, Text content, String etag, Optional<Author> author) {
        this.id = id;
        this.publicationDate = publicationDate;
        this.title = title;
        this.content = content;
        this.etag = etag;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public DateTime getPublicationDate() {
        return publicationDate;
    }

    public Text getTitle() {
        return title;
    }

    public Text getContent() {
        return content;
    }

    public String getEtag() {
        return etag;
    }

    public Author getAuthor() {
        return author.orElse(null);
    }
}
