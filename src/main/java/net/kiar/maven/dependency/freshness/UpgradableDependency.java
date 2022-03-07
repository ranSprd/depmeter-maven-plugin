package net.kiar.maven.dependency.freshness;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.api.UpdateScope;

/**
 *
 * @author ran
 */
public class UpgradableDependency {
    
    private static final boolean allowSnapshots = true;
    
    
    private final ArtifactVersions allVersions;
    private final Dependency dependency;
    
    private ArtifactVersion latestVersion;
    private ArtifactVersion usedVersion;
    
    
    /**
     * Create an instance and compute the current version and the latest available.
     * @param dependency
     * @param versions
     * @return 
     */
    public static UpgradableDependency create(Dependency dependency, ArtifactVersions versions) {
        UpgradableDependency result = new UpgradableDependency(dependency, versions);
        if (versions.isCurrentVersionDefined()) {
            result.setUsedVersion( versions.getCurrentVersion());
            result.setLatestVersion( versions.getNewestUpdate(UpdateScope.ANY, allowSnapshots));
        } else {
            ArtifactVersion latest = null;
            final ArtifactVersion newestVersionInRange
                    = versions.getNewestVersion(versions.getArtifact().getVersionRange(), allowSnapshots);
            if (newestVersionInRange != null) {
                latest = versions.getNewestUpdate(newestVersionInRange, UpdateScope.ANY, allowSnapshots);
                if (latest != null && ArtifactVersions.isVersionInRange(latest, versions.getArtifact().getVersionRange())) {
                    latest = null;
                }
            }
            result.setUsedVersion( newestVersionInRange);
            result.setLatestVersion( latest);
        }
        
        return result;
    }
    

    public UpgradableDependency(Dependency dependency, ArtifactVersions allVersions) {
        this.allVersions = allVersions;
        this.dependency = dependency;
    }

    public ArtifactVersions getAllVersions() {
        return allVersions;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public ArtifactVersion getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(ArtifactVersion latestVersion) {
        this.latestVersion = latestVersion;
    }

    public ArtifactVersion getUsedVersion() {
        return usedVersion;
    }
    
    public void setUsedVersion(ArtifactVersion usedVersion) {
        this.usedVersion = usedVersion;
    }
    
    public boolean isUpgradable() {
        return true;
    }
    
    
    
}
