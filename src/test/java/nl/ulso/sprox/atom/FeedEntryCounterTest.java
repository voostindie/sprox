package nl.ulso.sprox.atom;

import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FeedEntryCounterTest {

    @Test
    public void countAllEntriesInFeed() throws Exception {
        final FeedEntryCounter entryCounter = new FeedEntryCounter();
        final XmlProcessor<Void> processor = createXmlProcessorBuilder(Void.class)
                .addControllerObject(entryCounter)
                .buildXmlProcessor();
        processor.execute(getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
        assertThat(entryCounter.getNumberOfEntries(), is(25));
    }

    @Test
    public void countAllEntriesInFeedWithResult() throws Exception {
        final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
                .addControllerClass(BetterFeedEntryCounter.class)
                .buildXmlProcessor();
        final int numberOfEntries = processor.execute(
                getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
        assertThat(numberOfEntries, is(25));
    }

    @Test
    public void countOnlyEntriesPublishedIn2013InFeed() throws Exception {
        final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
                .addControllerClass(FeedEntryFrom2013Counter.class)
                .buildXmlProcessor();
        final int numberOfEntries = processor.execute(
                getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
        assertThat(numberOfEntries, is(1));
    }

    @Test
    public void countOnlyEntriesPublishedIn2013InFeedWithCustomParser() throws Exception {
        final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
                .addControllerClass(BetterFeedEntryFrom2013Counter.class)
                .addParser(new DateTimeParser())
                .buildXmlProcessor();
        final int numberOfEntries = processor.execute(
                getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
        assertThat(numberOfEntries, is(1));
    }
}
