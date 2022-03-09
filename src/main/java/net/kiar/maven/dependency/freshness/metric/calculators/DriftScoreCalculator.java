package net.kiar.maven.dependency.freshness.metric.calculators;

import java.util.List;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;

/**
 *
 * @author ran
 */
public class DriftScoreCalculator {

    /**
     * Drift Score
     * The drift score metric is a weighted metric. For each version step a 
     * counter is increased. The value of that increase depends on the most 
     * significant version part (major, minor, patch). Whereby 
     *      major = 1.0
     *      minor = 0.1
     *      patch = 0.01
     * 
     * see https://nimbleindustries.io/2020/08/08/drift-score-the-dependency-drift-metric/
     * 
     * @param used
     * @param newer
     * 
     * @return a value between 0.0 ... MAX-DOUBLE
     */
    public static double compute(ArtifactVersion used, List<ArtifactVersion> newer) {
        if (used == null || newer == null || newer.isEmpty()) {
            return 0.0;
        }
        double driftScore = 0.0;
        ArtifactVersion start = used;
        newer.sort(new MavenVersionComparator());
        for(ArtifactVersion end : newer) {
            driftScore += driftScore(start, end);
            start = end;
        }
        
        return driftScore;
    }
    
    public static double computeDriftScore(ArtifactVersion a, ArtifactVersion b) {
        if (a == null || b == null) {
            return 0.0;
        }
        return driftScore(a, b);
    }
    
    private static double driftScore(ArtifactVersion a, ArtifactVersion b) {
        if (a.getMajorVersion() != b.getMajorVersion()) {
            return 1.0;
        } else if (a.getMinorVersion() != b.getMinorVersion()) {
            return 0.1;
        } else if (a.getIncrementalVersion() != b.getIncrementalVersion()) {
            return 0.01;
        }
        
        return 0.0;
    }
}
