package net.kiar.maven.dependency.freshness.metric.calculators;

import net.kiar.maven.dependency.freshness.metric.calculators.VersionNumberDeltaMetricCalculator;
import java.util.List;
import net.kiar.maven.dependency.freshness.metric.VersionNumberDelta;
import net.kiar.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author ran
 */
public class VersionNumberDeltaMetricCalculatorTest {
    
    @Test
    public void testCalcDelta1() {
        ArtifactVersion used = new DefaultArtifactVersion("1.0");
        List<ArtifactVersion> versions = DependencyBuilder.createArticArtifactVersions("1.0.1", "2.1.1");
        
        VersionNumberDelta delta = VersionNumberDeltaMetricCalculator.compute(used, versions);
        assertNotNull(delta);
        assertEquals(1, delta.getMajor());
        assertEquals(0, delta.getMinor());
        assertEquals(1, delta.getPatch());
    }
    
    @Test
    public void testCalcDelta2() {
        ArtifactVersion used = new DefaultArtifactVersion("1.2.0");
        List<ArtifactVersion> versions = DependencyBuilder.createArticArtifactVersions("1.2.1", "1.3.0", "1.3.1");
        
        VersionNumberDelta delta = VersionNumberDeltaMetricCalculator.compute(used, versions);
        assertNotNull(delta);
        assertEquals(0, delta.getMajor());
        assertEquals(1, delta.getMinor());
        assertEquals(2, delta.getPatch());
    }
    
    
    @Test
    public void testCalcDeltaUnsorted() {
        ArtifactVersion used = new DefaultArtifactVersion("1.2.0");
        List<ArtifactVersion> versions = DependencyBuilder.createArticArtifactVersions("1.3.1", "1.2.1", "1.3.5");
        
        VersionNumberDelta delta = VersionNumberDeltaMetricCalculator.compute(used, versions);
        assertNotNull(delta);
        assertEquals(0, delta.getMajor());
        assertEquals(1, delta.getMinor());
        assertEquals(5, delta.getPatch());
        
    }
    
}
