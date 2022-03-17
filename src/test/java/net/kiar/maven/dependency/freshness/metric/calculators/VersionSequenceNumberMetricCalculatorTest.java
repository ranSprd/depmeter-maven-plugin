package net.kiar.maven.dependency.freshness.metric.calculators;

import java.util.List;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class VersionSequenceNumberMetricCalculatorTest {
    
    @Test
    public void testInvalidInput() {
        assertEquals(0, VersionSequenceNumberMetricCalculator.compute(null));
        assertEquals(0, VersionSequenceNumberMetricCalculator.compute(List.of()));
    }
    
    @Test
    public void testHappyCase() {
        assertEquals(1, VersionSequenceNumberMetricCalculator.compute(
                List.of( new DefaultArtifactVersion("1.0") )));
    }
    
}
