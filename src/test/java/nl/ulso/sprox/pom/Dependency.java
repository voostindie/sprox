package nl.ulso.sprox.pom;

/**
 *
 */
public class Dependency {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String scope;
    private final String classifier;

    public Dependency(String groupId, String artifactId, String version, String scope, String classifier) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
        this.classifier = classifier;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getScope() {
        return scope;
    }

    public String getClassifier() {
        return classifier;
    }
}
