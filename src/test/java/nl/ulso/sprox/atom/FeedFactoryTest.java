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

import nl.ulso.sprox.ElementNameResolver;
import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.XmlProcessorBuilder;
import nl.ulso.sprox.XmlProcessorException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FeedFactoryTest {

    @Test
    public void testFeedFactory() throws Exception {
        runTestWith(FeedFactory.class, null);
    }

    @Test
    public void testFeedFactoryWithAbbreviatedShorthands() throws Exception {
        runTestWith(FeedFactoryWithAbbreviatedShorthands.class, null);
    }

    @Test
    public void testFeedFactoryWithCustomNamesForMethodsAndParameters() throws Exception {
        runTestWith(FeedFactoryWithCustomNamesForMethodsAndParameters.class, new ElementNameResolver() {
            final int METHOD_PREFIX_LENGTH = "create".length();

            @Override
            public String fromParameter(Class<?> controllerClass, Method method, Parameter parameter) {
                if (parameter.getName().equals("publicationDate") && parameter.getType().getSimpleName().equals("DateTime")) {
                    return "published";
                }
                return parameter.getName();
            }

            @Override
            public String fromMethod(Class<?> controllerClass, Method method) {
                System.out.println(method.getName());
                return method.getName().substring(METHOD_PREFIX_LENGTH).toLowerCase();
            }
        });

    }

    private void runTestWith(Class<?> factoryClass, ElementNameResolver resolver) throws XmlProcessorException {
        final XmlProcessorBuilder<Feed> builder = createXmlProcessorBuilder(Feed.class);
        if (resolver != null) {
            builder.setElementNameResolver(resolver);
        }
        final XmlProcessor<Feed> processor = builder
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
