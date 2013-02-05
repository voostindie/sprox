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

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Nullable;
import nl.ulso.sprox.Source;
import org.joda.time.DateTime;

import java.util.List;

import static nl.ulso.sprox.atom.TextType.HTML;
import static nl.ulso.sprox.atom.TextType.TEXT;

public class FeedFactory {
    @Node("feed")
    public Feed createFeed(@Source("title") Text title, @Source("subtitle") Text subtitle,
                           Author author, List<Entry> entries) {
        return new Feed(title, subtitle, author, entries);
    }

    @Node("author")
    public Author createAuthor(@Node("name") String name, @Node("uri") String uri,
                               @Node("email") String email) {
        return new Author(name, uri, email);
    }

    @Node("entry")
    public Entry createEntry(@Node("id") String id, @Node("published") DateTime publicationDate,
                             @Source("title") Text title, @Source("content") Text content,
                             @Nullable Author author) {
        return new Entry(id, publicationDate, title, content, author);
    }

    @Node("title")
    public Text createTitle(@Nullable @Attribute("type") TextType textType,
                            @Node("title") String content) {
        return createText(textType, content);
    }

    @Node("subtitle")
    public Text createSubtitle(@Nullable @Attribute("type") TextType textType,
                               @Node("subtitle") String content) {
        return createText(textType, content);
    }

    @Node("content")
    public Text createContent(@Nullable @Attribute("type") TextType type,
                              @Node("content") String content) {
        return createText(type, content);
    }

    private Text createText(TextType textType, String content) {
        if (textType == null || textType == TEXT) {
            return new SimpleText(content);
        }
        if (textType == HTML) {
            return new HtmlText(content);
        }
        // XHTML is not supported
        throw new IllegalArgumentException("Unsupported text type: " + textType);
    }
}
