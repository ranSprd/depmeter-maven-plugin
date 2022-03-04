package net.kiar.maven.dependency.freshness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.api.UpdateScope;

/**
 *
 * @author ran
 */
public class UpgradableDependencies {
    
    private static final boolean allowSnapshots = true;
    
    
    public static UpgradableDependencies select(Map<Dependency, ArtifactVersions> dependencies) {
        
        List<String> withUpdates = new ArrayList<>();
        List<String> usingCurrent = new ArrayList<>();
        Iterator i = dependencies.values().iterator();
        while (i.hasNext()) {
            ArtifactVersions versions = (ArtifactVersions) i.next();
            ArtifactVersion latest;
            if (versions.isCurrentVersionDefined()) {
                latest = versions.getNewestUpdate(UpdateScope.ANY, allowSnapshots);
            } else {
                ArtifactVersion newestVersionInRange
                        = versions.getNewestVersion(versions.getArtifact().getVersionRange(), allowSnapshots);
                latest = newestVersionInRange == null ? null
                        : versions.getNewestUpdate(newestVersionInRange, UpdateScope.ANY, allowSnapshots);
                if (latest != null
                        && ArtifactVersions.isVersionInRange(latest, versions.getArtifact().getVersionRange())) {
                    latest = null;
                }
            }
            
            
        }
        
        
        return new UpgradableDependencies();
    }
    
}
