package io.github.ranSprd.maven.dependency.freshness.metric;

import io.github.ranSprd.maven.dependency.freshness.metric.calculators.VersionSequenceNumberMetricCalculator;
import java.util.List;
import io.github.ranSprd.maven.dependency.freshness.UpgradableDependency;
import io.github.ranSprd.maven.dependency.freshness.metric.calculators.DriftScoreCalculator;
import io.github.ranSprd.maven.dependency.freshness.metric.calculators.VersionNumberDeltaMetricCalculator;
import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 * 
 * Calcultes metrics for an artifact.
 * 
 * @author ran
 */
public class ArtifactDependencyMetrics {
    
    private final UpgradableDependency dependency;
    
    private final int versionSequenceNumber;
    private final double driftScore;
    private final VersionDelta delta;

    public ArtifactDependencyMetrics(UpgradableDependency dependency) {
        this.dependency = dependency;
        
        List<ArtifactVersion> newerVersions = dependency.getAllNewerVersions();
        this.versionSequenceNumber = VersionSequenceNumberMetricCalculator.compute( newerVersions);
        this.driftScore = DriftScoreCalculator.compute(dependency.getUsedVersion(), newerVersions);
        this.delta = VersionNumberDeltaMetricCalculator.compute(dependency.getUsedVersion(), newerVersions);
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

    /**
     * To calculate a Drift Score for a given artifact, we find all the 
     * releases of that artifact between the version you have specified and 
     * the most recent available. Then we sequentially subtract the difference 
     * between each of the artifact versions and increment a score counter.
     * 
     * https://nimbleindustries.io/2020/08/08/drift-score-the-dependency-drift-metric/
     * 
     * @return 
     */
    public double getDriftScore() {
        return driftScore;
    }

    public VersionDelta getDelta() {
        return delta;
    }

    public String getGroupId() {
        return dependency.getGroupId();
    }

    public String getArtifactId() {
        return dependency.getArtifactId();
    }

    public boolean isUpgradable() {
        return dependency.isUpgradable();
    }
    
}
