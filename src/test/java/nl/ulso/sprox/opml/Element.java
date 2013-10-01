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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Element implements Iterable<Element>, Visitable {
    private final String text;
    private final List<Element> elements;

    public Element(String text) {
        this(text, Collections.<Element>emptyList());
    }

    public Element(String text, List<Element> elements) {
        this.text = text;
        this.elements = unmodifiableList(new ArrayList<>(elements));
    }

    public String getText() {
        return text;
    }

    public int getNumberOfElements() {
        return elements.size();
    }

    public Element getElementAt(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        for (Element element : elements) {
            element.accept(visitor);
        }
    }
}
