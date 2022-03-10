package net.kiar.maven.dependency.freshness.metric;

import java.util.List;
import net.kiar.maven.dependency.freshness.metric.reducer.OverallMetricReducer;
import net.kiar.maven.dependency.freshness.metric.selectors.MetricsSelector;

/**
 *
 * @author ran
 */
public class Factory {
    
    
    
    
    public void x() {
        useEveryArtifact().reduce( summarize());
        
        group(byGroupId()).reduceGroup( max()).reduce(summarize());
    }
    
    
    private GroupedMetricsList group(MetricsSelector grouper) {
        return new GroupedMetricsList();
    }
    
    
    private CombinedMetricsList useEveryArtifact() {
        return new CombinedMetricsList();
    }
    
    private OverallMetricReducer summarize() {
        return null;
    }
    
    private OverallMetricReducer max() {
        return null;
    }
    
    private MetricsSelector byGroupId() {
        return new MetricsSelector();
    }
    
    
    public static class CombinedMetrics {
        private int metric1 = 0;
    }
    
    public static class CombinedMetricsList {
        private List<CombinedMetrics> list;
        
        public CombinedMetrics reduce(OverallMetricReducer reducer) {
            return new CombinedMetrics();
        }
    }
    
    public static class GroupedMetricsList {
        private List<List<CombinedMetrics>> metrics;
        
        public CombinedMetricsList reduceGroup(OverallMetricReducer reducer) {
            return new CombinedMetricsList();
        }
    }
    
    public static class Intermediate {
        
    }
    
    
}
