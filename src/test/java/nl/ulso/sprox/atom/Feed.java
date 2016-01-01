package nl.ulso.sprox.atom;

import java.util.*;

import static java.util.Collections.unmodifiableList;

public class Feed {
    private final Text title;
    private final Text subtitle;
    private final Author author;
    private final List<Entry> entries;

    public Feed(Text title, Text subtitle, Author author, List<Entry> entries) {
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.entries = unmodifiableList(new ArrayList<>(entries));
    }

    public Text getTitle() {
        return title;
    }

    public Text getSubtitle() {
        return subtitle;
    }

    public Author getAuthor() {
        return author;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
