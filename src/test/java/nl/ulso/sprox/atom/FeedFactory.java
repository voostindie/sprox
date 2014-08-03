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

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Namespace;
import nl.ulso.sprox.Namespaces;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Source;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

@Namespaces({
        @Namespace("http://www.w3.org/2005/Atom"),
        @Namespace(shorthand = "g", value = "http://schemas.google.com/g/2005")
})
public class FeedFactory {
    @Node("feed")
    public Feed createFeed(@Source("title") Text title, @Source("subtitle") Text subtitle,
                           Author author, List<Entry> entries) {
        return new Feed(title, subtitle, author, entries);
    }

    @Node("author")
    public Author createAuthor(@Node("name") String name, @Node("uri") String uri,
                               @Node("email") String email, Image image) {
        return new Author(name, uri, email, image);
    }

    @Node("entry")
    public Entry createEntry(@Node("id") String id, @Node("published") DateTime publicationDate,
                             @Source("title") Text title, @Source("content") Text content, Optional<Author> author) {
        return new Entry(id, publicationDate, title, content, author);
    }

    @Node("title")
    public Text createTitle(@Attribute("type") Optional<TextType> textType,
                            @Node("title") String content) {
        return createText(textType, content);
    }

    @Node("subtitle")
    public Text createSubtitle(@Attribute("type") Optional<TextType> textType,
                               @Node("subtitle") String content) {
        return createText(textType, content);
    }

    @Node("content")
    public Text createContent(@Attribute("type") Optional<TextType> type,
                              @Node("content") String content) {
        return createText(type, content);
    }

    @Node("g:image")
    public Image createImage(@Attribute("src") String src, @Attribute("width") Integer width,
                             @Attribute("height") Integer height) {
        return new Image(src, width, height);
    }

    private Text createText(Optional<TextType> textType, String content) {
        return textType.map(type -> {
            switch (type) {
                case TEXT:
                    return new SimpleText(content);
                case HTML:
                    return new HtmlText(content);
                default:
                    // XHTML is not supported
                    throw new IllegalArgumentException("Unsupported text type: " + textType);
            }
        }).orElse(new SimpleText(content));
    }
}
