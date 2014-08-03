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

package nl.ulso.sprox.pom;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Project {
    private final String groupId;
    private final String artifactId;
    private final List<Dependency> dependencies;

    public Project(String groupId, String artifactId, List<Dependency> dependencies) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.dependencies = Collections.unmodifiableList(dependencies);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }
}
