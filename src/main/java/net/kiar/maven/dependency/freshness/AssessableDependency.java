package net.kiar.maven.dependency.freshness;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.mojo.versions.api.ArtifactVersions;

/**
 *
 * @author ran
 */
public class AssessableDependency {
    
    private ArtifactVersions allVersions;
    private ArtifactVersion latest;

    public boolean isUpgradable() {
        return latest != null;
    }
}
