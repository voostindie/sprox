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

public class Author {
    private final String name;
    private final String uri;
    private final String email;

    public Author(String name, String uri, String email) {
        this.name = name;
        this.uri = uri;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getEmail() {
        return email;
    }
}
