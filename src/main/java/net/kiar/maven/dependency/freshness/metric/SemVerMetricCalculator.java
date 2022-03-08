package net.kiar.maven.dependency.freshness.metric;

import java.util.List;
import net.kiar.maven.dependency.freshness.UpgradableDependency;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;
import org.codehaus.mojo.versions.ordering.VersionComparator;

/**
 *
 * @author ran
 */
public class SemVerMetricCalculator {
    
    private static final VersionComparator versionComparator = new MavenVersionComparator();
    
    private final UpgradableDependency dependency;

    public SemVerMetricCalculator(UpgradableDependency dependency) {
        this.dependency = dependency;
    }
    

    public double getFreshnessMetricValue() {
        int majorUpdates = 0;
        if (dependency.isUpgradable()) {
            ArtifactVersion used = dependency.getUsedVersion();
            List<ArtifactVersion> newer = dependency.getAllNewerVersions();
            for(ArtifactVersion n : newer) {
                if (used.getMajorVersion() < n.getMajorVersion()) {
//                    major
                }
            }
            
        }
        return 0.0;
    }
    
}
