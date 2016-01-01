package nl.ulso.sprox.pom;

import java.util.List;

import static java.util.Collections.unmodifiableList;

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
        this.dependencies = unmodifiableList(dependencies);
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
