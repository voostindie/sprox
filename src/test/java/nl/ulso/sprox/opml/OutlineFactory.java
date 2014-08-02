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

package nl.ulso.sprox.opml;

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Recursive;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

public class OutlineFactory {

    @Node("opml")
    public Outline createOutline(@Node("title") String title, @Node("dateCreated") DateTime creationDate,
                                 @Node("dateModified") DateTime modificationDate, List<Element> elements) {
        return new Outline(title, creationDate, modificationDate, elements);
    }

    @Recursive
    @Node("outline")
    public Element createElement(@Attribute("text") String text, Optional<List<Element>> elements) {
        return new Element(text, elements);
    }
}
