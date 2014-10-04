/*
 * Copyright 2013-2014 Vincent Oostindië
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
import nl.ulso.sprox.XmlProcessorException;
import org.junit.Test;

import java.util.List;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FeedFactoryTest {

    @Test
    public void testFeedFactory() throws Exception {
        runTestWith(FeedFactory.class);
    }

    @Test
    public void testFeedFactoryWithAbbreviatedShorthands() throws Exception {
        runTestWith(FeedFactoryWithAbbreviatedShorthands.class);
    }

    private void runTestWith(Class<?> factoryClass) throws XmlProcessorException {
        final XmlProcessor<Feed> processor = createXmlProcessorBuilder(Feed.class)
                .addControllerClass(factoryClass)
                .addParser(new DateTimeParser())
                .addParser(new TextTypeParser())
                .buildXmlProcessor();
        final Feed feed = processor.execute(
                getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
        assertNotNull(feed);
        assertThat(feed.getTitle().toString(), is("Google Webmaster Central Blog"));
        assertThat(feed.getSubtitle().toString(), is("Official news on crawling and indexing sites for the Google index."));
        final Author feedAuthor = feed.getAuthor();
        assertNotNull(feedAuthor);
        assertThat(feedAuthor.getName(), is("Mariya Moeva"));
        assertNotNull(feedAuthor.getImage());
        final List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        assertThat(entries.size(), is(25));
        final Entry firstEntry = entries.get(0);
        assertThat(firstEntry.getContent().toString(), containsString("Here’s what it means for webmasters:"));
        assertThat(firstEntry.getEtag(), is("W/\"A0YER3kzeyp7ImA9WhNbGUU.\""));
    }
}
