/*
 * Copyright 2013 Vincent Oostindië
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.sprox.atom;

import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import static nl.ulso.sprox.XmlProcessorFactory.createXmlProcessorBuilder;
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
