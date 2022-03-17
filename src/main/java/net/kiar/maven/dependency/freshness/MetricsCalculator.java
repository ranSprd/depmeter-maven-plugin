package net.kiar.maven.dependency.freshness;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.kiar.maven.dependency.freshness.metric.ArtifactDependencyMetrics;

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
                    .flatMap(list -> list.stream())
                    .mapToDouble(metric -> metric.getDriftScore())
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
                    .mapToDouble(list -> maxDriftScore(list))
                    .sum();
    }
    
    /**
     * The sum of all version sequence numbers from all artifacts.
     * @return 
     */
    public double overallVersionSequenceCount() {
        return metricsByGroupId.values().stream()
                    .flatMap(list -> list.stream())
                    .mapToInt(metric -> metric.getVersionSequenceNumber())
                    .sum();
    }
    
    public double packageVersionSequenceCount() {
        return metricsByGroupId.values().stream()
                    .mapToInt(list -> maxVersionSequenceNumber(list))
                    .sum();
    }
    
    
    
    private double maxDriftScore(List<ArtifactDependencyMetrics> metrics) {
        return metrics.stream()
                .mapToDouble(metric -> metric.getDriftScore())
                .max()
                .orElse(0.0);
    }
    
    private int maxVersionSequenceNumber(List<ArtifactDependencyMetrics> metrics) {
        return metrics.stream()
                .mapToInt(metric -> metric.getVersionSequenceNumber())
                .max()
                .orElse(0);
    }
    
    
}
