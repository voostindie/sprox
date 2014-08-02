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

import org.joda.time.DateTime;

import java.util.Optional;

public class Entry {
    private final String id;
    private final DateTime publicationDate;
    private final Text title;
    private final Text content;
    private final Optional<Author> author;

    public Entry(String id, DateTime publicationDate, Text title, Text content, Optional<Author> author) {
        this.id = id;
        this.publicationDate = publicationDate;
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public DateTime getPublicationDate() {
        return publicationDate;
    }

    public Text getTitle() {
        return title;
    }

    public Text getContent() {
        return content;
    }

    public Author getAuthor() {
        return author.orElse(null);
    }
}
