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

import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

public class Outline extends Element {
    private final DateTime creationDate;
    private final DateTime modificationDate;

    public Outline(String title, DateTime creationDate, DateTime modificationDate, List<Element> elements) {
        super(title, Optional.of(elements));
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    public String getTitle() {
        return getText();
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        for (Element element : this) {
            element.accept(visitor);
        }
    }
}
