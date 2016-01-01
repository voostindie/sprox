package nl.ulso.sprox.atom;

import nl.ulso.sprox.Node;

/**
 * Counts all the entries in an Atom feed
 */
public class FeedEntryCounter {
    private int numberOfEntries;

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry() {
        numberOfEntries++;
    }
}
