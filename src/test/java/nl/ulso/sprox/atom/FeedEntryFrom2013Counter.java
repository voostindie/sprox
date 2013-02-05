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

import nl.ulso.sprox.Node;

/**
 * Counts all the entries in an Atom feed from 2013.
 */
public class FeedEntryFrom2013Counter {
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry(@Node("published") String publicationDate) {
        if (Integer.parseInt(publicationDate.substring(0, 4)) == 2013) {
            numberOfEntries++;
        }
    }
}
