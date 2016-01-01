package nl.ulso.sprox.atom;

import nl.ulso.sprox.Node;

import static java.lang.Integer.parseInt;

/**
 * Counts all the entries in an Atom feed from 2013.
 */
public class FeedEntryFrom2013Counter {
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry(@Node("published") String publicationDate) {
        if (parseInt(publicationDate.substring(0, 4)) == 2013) {
            numberOfEntries++;
        }
    }
}
