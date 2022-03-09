package net.kiar.maven.dependency.freshness.metric.calculators;

import java.util.List;
import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 *
 * @author ran
 */
public class VersionSequenceNumberMetricCalculator {


    /**
     * Calculate the Version Sequence Number. 
     * The difference between two separate versions of a dependency can be 
     * expressed by the difference of the version sequence numbers of 
     * two releases. This measurement does not necessarily take into account 
     * the version number of a dependency, but can also employ the release date 
     * of the dependency to order difference versions. I.e., for a dependency 
     * with the versions (dn, dn+1, dn+2) ordered by release date, the version 
     * sequence distance between dn and dn+2 is 2. 
     * Consideration: Dependencies with short release cycles are penalized 
     * by this measurement, as the version sequence distance is relatively 
     * high compared to other dependencies.
     * 
     * https://ericbouwers.github.io/papers/icse15.pdf
     * 
     * @param newerVersions list of all versions that are newer as the currently used
     * 
     * @return a value between 0...MAX-INT
     */
    public static int compute(List<ArtifactVersion> newerVersions) {
        if (newerVersions == null || newerVersions.isEmpty()) {
            return 0;
        }
        return newerVersions.size();
    }
    
}
