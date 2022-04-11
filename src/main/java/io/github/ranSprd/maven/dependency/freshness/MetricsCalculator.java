package io.github.ranSprd.maven.dependency.freshness;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.github.ranSprd.maven.dependency.freshness.metric.ArtifactDependencyMetrics;
import io.github.ranSprd.maven.dependency.freshness.metric.VersionDelta;
import java.util.Collection;

/**
 *
 * @author ran
 */
public class MetricsCalculator {
    
    
    private final Map<String, List<ArtifactDependencyMetrics>> metricsByGroupId;
    
    public static MetricsCalculator get(UpgradableDependencies deps) {
        
        Map<String, List<ArtifactDependencyMetrics>> mapped = deps.getAllDependencies().stream()
                .map(dep -> new ArtifactDependencyMetrics(dep))
                .collect(Collectors.groupingBy(ArtifactDependencyMetrics::getGroupId));
        return new MetricsCalculator(mapped);
    }

    private MetricsCalculator(Map<String, List<ArtifactDependencyMetrics>> metricsByGroupId) {
        this.metricsByGroupId = metricsByGroupId;
    }

    public Map<String, List<ArtifactDependencyMetrics>> getMetricsByGroupId() {
        return metricsByGroupId;
    }
    
    /**
     * Summarize the scores of all artifacts 
     * @return 
     */
    public double overallDriftScore() {
        return metricsByGroupId.values().stream()
                    .flatMap(Collection::stream)
                    .mapToDouble(ArtifactDependencyMetrics::getDriftScore)
                    .sum();
    }
    
    /**
     * It summarize the scores of all artifacts. For artifacts with the same 
     * group-id, only the artifact with the highest score is used.
     * 
     * @return 
     */
    public double packageDriftScore() {
        return metricsByGroupId.values().stream()
                    .mapToDouble(this::maxDriftScore)
                    .sum();
    }
    
    /**
     * The sum of all version sequence numbers from all artifacts.
     * @return 
     */
    public int overallVersionSequenceCount() {
        return metricsByGroupId.values().stream()
                    .flatMap(Collection::stream)
                    .mapToInt(ArtifactDependencyMetrics::getVersionSequenceNumber)
                    .sum();
    }
    
    public int packageVersionSequenceCount() {
        return metricsByGroupId.values().stream()
                    .mapToInt(this::maxVersionSequenceNumber)
                    .sum();
    }
    
    public VersionDelta overallVersionDelta() {
        VersionDelta result = new VersionDelta();
        for(List<ArtifactDependencyMetrics> l : metricsByGroupId.values()) {
            for(ArtifactDependencyMetrics m : l) {
                result.accumulate(m.getDelta());
            } 
        }
        return result;
    }
    
    
    
    private double maxDriftScore(List<ArtifactDependencyMetrics> metrics) {
        return metrics.stream()
                .mapToDouble(ArtifactDependencyMetrics::getDriftScore)
                .max()
                .orElse(0.0);
    }
    
    private int maxVersionSequenceNumber(List<ArtifactDependencyMetrics> metrics) {
        return metrics.stream()
                .mapToInt(ArtifactDependencyMetrics::getVersionSequenceNumber)
                .max()
                .orElse(0);
    }
    
    
}
