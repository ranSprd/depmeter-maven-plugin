package net.kiar.maven.dependency.freshness.metric.calculators;

import net.kiar.maven.dependency.freshness.testhelper.DependencyBuilder;
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
        
    }
    
}
