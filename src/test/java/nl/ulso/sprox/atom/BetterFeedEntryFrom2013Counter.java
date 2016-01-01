package nl.ulso.sprox.atom;

import nl.ulso.sprox.Node;
import org.joda.time.DateTime;

import static org.joda.time.DateTime.parse;

/**
 * Counts all the entries in an Atom feed from 2013.
 */
public class BetterFeedEntryFrom2013Counter {
    private static final DateTime JANUARY_1ST_2013 = parse("2013-01-01");
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry(@Node("published") DateTime publicationDate) {
        if (publicationDate.isAfter(JANUARY_1ST_2013)) {
            numberOfEntries++;
        }
    }
}
