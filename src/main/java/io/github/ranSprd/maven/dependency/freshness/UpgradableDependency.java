package io.github.ranSprd.maven.dependency.freshness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
//import org.codehaus.mojo.versions.api.UpdateScope;
import static java.util.Optional.empty;
import java.util.stream.Collectors;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.mojo.versions.api.Segment;

/**
 *
 * @author ran
 */
public class UpgradableDependency {
    
    private static final boolean ALLOW_SNAPSHOTS = false;
    
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
//            result.setLatestVersion( versions.getNewestUpdate(UpdateScope.ANY, ALLOW_SNAPSHOTS));
            result.setLatestVersion( versions.getNewestUpdate(empty(), ALLOW_SNAPSHOTS));
        } else {
            ArtifactVersion latest = null;
            final VersionRange versionRange = versions.getArtifact().getVersionRange();
            final ArtifactVersion newestVersionInRange
                    = versions.getNewestVersion(versionRange, ALLOW_SNAPSHOTS);
            if (newestVersionInRange != null) {
//                latest = versions.getNewestUpdate(newestVersionInRange, UpdateScope.ANY, ALLOW_SNAPSHOTS);
                latest = versions.getNewestUpdate(newestVersionInRange, empty(), ALLOW_SNAPSHOTS);
                if (latest != null && versionRange != null && ArtifactVersions.isVersionInRange(latest, versionRange)) {
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

    public String getGroupId() {
        return allVersions.getGroupId();
    }

    public String getArtifactId() {
        return allVersions.getArtifactId();
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
        if (usedVersion != null && this.allVersions != null) {
            this.allVersions.setCurrentVersion(usedVersion);
        }
    }
    
    
    /**
     * get all versions newer as the current used version
     * @return 
     */
    public List<ArtifactVersion> getAllNewerVersions() {
        if (usedVersion != null) {
            ArtifactVersion[] result = allVersions.getAllUpdates(usedVersion, empty(), ALLOW_SNAPSHOTS);
            if (result != null && result.length > 0) {
                  return Arrays.stream(result)
                          .filter(v -> !v.equals(usedVersion))
                          .collect(Collectors.toList());
            }
        }
        return List.of();
    }
    
    public List<ArtifactVersion> getAllNewerMajorVersions() {
        if (usedVersion != null) {
//            ArtifactVersion[] result = allVersions.getAllUpdates(usedVersion, Segment.MAJOR);
            ArtifactVersion[] result = allVersions.getAllUpdates(usedVersion, Optional.of(Segment.MAJOR), false);
            if (result != null && result.length > 0) {
                return Arrays.asList(result);
            }
        }
        return List.of();
    }
    
    
    /**
     * 
     * @return true if a usedVersion and a latestVersion are known. Both versions
     * are not identical.
     */
    public boolean isUpgradable() {
        if (usedVersion == null) {
            return false;
        }
        if (latestVersion == null) {
            return false;
        }
        return !usedVersion.toString().equals( latestVersion.toString());
    }
    
    
    
}
