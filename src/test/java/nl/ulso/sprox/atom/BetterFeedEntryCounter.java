package nl.ulso.sprox.atom;

import nl.ulso.sprox.Node;

/**
 * Counts all the entries in an Atom feed
 */
public class BetterFeedEntryCounter {
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry() {
        numberOfEntries++;
    }
}
