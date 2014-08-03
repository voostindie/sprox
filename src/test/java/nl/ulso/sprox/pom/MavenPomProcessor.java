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

import nl.ulso.sprox.Node;

import java.util.List;

/**
 *
 */
public class MavenPomProcessor {

    @Node("project")
    public Project createProject(@Node("groupId") String groupId, @Node("artifactId") String artifactId,
                                 List<Dependency> dependencies) {
        return new Project(groupId, artifactId, dependencies);
    }

    @Node("dependency")
    public Dependency createDependency(@Node("groupId") String groupId, @Node("artifactId") String artifactId,
                                       @Node("version") String version, @Node("scope") String scope) {
        return new Dependency(groupId, artifactId, version, scope, null);
    }
}
