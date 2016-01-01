package nl.ulso.sprox.pom;

import nl.ulso.sprox.Node;

import java.util.List;

public class MavenPomProcessor {

    @Node
    public Project project(@Node String groupId, @Node String artifactId,
                           List<Dependency> dependencies) {
        return new Project(groupId, artifactId, dependencies);
    }

    @Node
    public Dependency dependency(@Node String groupId, @Node String artifactId,
                                 @Node String version, @Node String scope) {
        return new Dependency(groupId, artifactId, version, scope, null);
    }
}
