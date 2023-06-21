package io.github.ranSprd.maven.dependency.freshness.metric.calculators;

import io.github.ranSprd.maven.dependency.freshness.testhelper.DependencyBuilder;
import java.util.List;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class DriftScoreCalculatorTest {
    
    @Test
    public void testMetric() {
        
        assertEquals(1.01, DriftScoreCalculator.compute(
                                            new DefaultArtifactVersion("1.0"), 
                                       DependencyBuilder.createArticArtifactVersions("1.0.1", "2.1.1")), 0.0);
        
        assertEquals(1.11, DriftScoreCalculator.compute(
                                            new DefaultArtifactVersion("2.4.5"), 
                                       DependencyBuilder.createArticArtifactVersions("2.4.6", "2.5.0", "3.0.0")), 0.0);
        
        assertEquals(0.005, DriftScoreCalculator.compute(
                                            new DefaultArtifactVersion("1.0.0-SNAPSHOT"), 
                                       DependencyBuilder.createArticArtifactVersions("1.0.0")), 0.0);
        
        assertEquals(0.0, DriftScoreCalculator.compute(
                                            new DefaultArtifactVersion("1.0.0"), 
                                       DependencyBuilder.createArticArtifactVersions("1.0.0-SNAPSHOT")), 0.0);
        assertEquals(2.005, DriftScoreCalculator.compute(
                                            new DefaultArtifactVersion("1"), 
                                       DependencyBuilder.createArticArtifactVersions("2.0.0-SNAPSHOT", "3-SNAPSHOT", "3.0")), 0.0);
        
    }
    @Test
    public void testInvalidInput() {
        
        assertEquals(0.0, DriftScoreCalculator.compute(
                                        null,                                            
                                       DependencyBuilder.createArticArtifactVersions("1.0.1", "2.1.1")), 0.0);
        assertEquals(0.0, DriftScoreCalculator.compute(
                                            new DefaultArtifactVersion("1.0.0"), 
                                       List.of()), 0.0);
        assertEquals(0.0, DriftScoreCalculator.compute(
                                            new DefaultArtifactVersion("1.0.0"), 
                                       null), 0.0);
        assertEquals(0.0, DriftScoreCalculator.compute(
                                            null, 
                                       null), 0.0);
    }
    
}
