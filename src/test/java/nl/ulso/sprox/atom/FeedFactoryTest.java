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

import java.util.List;

import static nl.ulso.sprox.XmlProcessorFactory.createXmlProcessorBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FeedFactoryTest {

    @Test
    public void testFeedBuilder() throws Exception {
        final XmlProcessor<Feed> processor = createXmlProcessorBuilder(Feed.class)
                .addControllerClass(FeedFactory.class)
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
        final List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        assertThat(entries.size(), is(25));
        assertThat(entries.get(0).getContent().toString(), containsString("Here’s what it means for webmasters:"));
    }
}
