package net.kiar.maven.dependency.freshness.metric;

import net.kiar.maven.dependency.freshness.metric.reducer.OverallMetricReducer;

/**
 *
 * @author ran
 */
public class Factory {
    
    
    public void x() {
        combineArtifactsByGroupId(summarize(), max());
        
        useEveryArtifact(summarize());
    }
    
    
    private void combineArtifactsByGroupId(OverallMetricReducer outerReducer, OverallMetricReducer innerReducer) {
        
    }
    
    
    private void useEveryArtifact(OverallMetricReducer reducer) {
        
    }
    
    private OverallMetricReducer summarize() {
        return null;
    }
    
    private OverallMetricReducer max() {
        return null;
    }
    
    
}
