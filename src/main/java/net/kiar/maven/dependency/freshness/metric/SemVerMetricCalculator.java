package net.kiar.maven.dependency.freshness.metric;

import net.kiar.maven.dependency.freshness.metric.calculators.VersionSequenceNumberMetricCalculator;
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
    
    private final UpgradableDependency dependency;
    
    private final int versionSequenceNumber;

    public SemVerMetricCalculator(UpgradableDependency dependency) {
        this.dependency = dependency;
        
        this.versionSequenceNumber = VersionSequenceNumberMetricCalculator.compute( dependency.getAllNewerVersions());
    }

    /**
     * Version Sequence Number.
     * This simple metric computes the number of releases between 2 versions.
     * 
     * https://ericbouwers.github.io/papers/icse15.pdf
     * 
     * @return a value between 0...MAX-INT
     */
    public int getVersionSequenceNumber() {
        return versionSequenceNumber;
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
