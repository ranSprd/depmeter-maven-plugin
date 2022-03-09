package net.kiar.maven.dependency.freshness.metric.calculators;

import java.util.List;
import net.kiar.maven.dependency.freshness.metric.VersionNumberDelta;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;

/**
 *
 * @author ran
 */
public class VersionNumberDeltaMetricCalculator {
    
    /**
     * Version Number Delta.
     * A distance can be computed by comparing the version numbers of the 
     * releases of a dependency. Comparing two version number tuples can be 
     * done by calculating the delta of all version number tuples between two 
     * releases. A version number is defined as a tuple (x, y, x) where x 
     * signifies the major version number, y the minor version number and x 
     * the patch version number. The function v returns the version numbers 
     * tuple for a version of a dependency.
     * The delta is defined as the absolute difference between the 
     * highest-order version number which has changed compared to the previous 
     * version number tuple. To compare multiple consecutive version number 
     * tuples, the deltas between individual versions are added like normal 
     * vectors. 
     * For example, two consecutive versions of a dependency v(dn) = (1, 2, 2) 
     * and v(dn+1) = (1, 3, 0) results in the version delta distance (0, 1, 0).
     * 
     * https://ericbouwers.github.io/papers/icse15.pdf
     * 
     * @param used currently used version
     * @param newer the next version
     * 
     * @return a delta tuple as distance metric between the versions
     */
    public static VersionNumberDelta compute(ArtifactVersion used, List<ArtifactVersion> newer) {
        VersionNumberDelta delta = new VersionNumberDelta();
        if (used == null || newer == null || newer.isEmpty()) {
            return delta;
        }
        
        ArtifactVersion start = used;
        newer.sort(new MavenVersionComparator());
        for(ArtifactVersion end : newer) {
            delta.accumulateDelta(start, end);
            start = end;
        }
        return delta;
    }
    
}
