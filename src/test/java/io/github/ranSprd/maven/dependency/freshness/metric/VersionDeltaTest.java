package io.github.ranSprd.maven.dependency.freshness.metric;

import io.github.ranSprd.maven.dependency.freshness.metric.VersionDelta;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class VersionDeltaTest {
    
    @Test
    public void testComputeDelta() {
        testDelta("1.2.0", "1.2.0", 0, 0, 0);
        testDelta("", "1.2.0", 1, 0, 0);
        testDelta("2.0.1", "", 2, 0, 0);
    }
    
    @Test
    public void testAccumulate() {
        VersionDelta delta1 = new VersionDelta();
        delta1.accumulate(null);
        assertEquals(0, delta1.getMajor());
        assertEquals(0, delta1.getMinor());
        assertEquals(0, delta1.getPatch());
        
        VersionDelta delta2 = new VersionDelta(0, 0, 0);
        delta2.accumulate(new VersionDelta(2, 0, 0));
        assertEquals(2, delta2.getMajor());
        assertEquals(0, delta2.getMinor());
        assertEquals(0, delta2.getPatch());
    }
    
    @Test
    public void testAccumulateDelta() {
        VersionDelta delta = new VersionDelta();
        assertEquals(0, delta.getMajor());
        assertEquals(0, delta.getMinor());
        assertEquals(0, delta.getPatch());
        
        delta.accumulateDelta(null, null);
        assertEquals(0, delta.getMajor());
        assertEquals(0, delta.getMinor());
        assertEquals(0, delta.getPatch());
        
        delta.accumulateDelta(new DefaultArtifactVersion("2.0.0"), null);
        assertEquals(0, delta.getMajor());
        assertEquals(0, delta.getMinor());
        assertEquals(0, delta.getPatch());
        
        delta.accumulateDelta(null, new DefaultArtifactVersion("2.0.0"));
        assertEquals(0, delta.getMajor());
        assertEquals(0, delta.getMinor());
        assertEquals(0, delta.getPatch());
        
        delta.accumulateDelta(new DefaultArtifactVersion("1.0.0"), new DefaultArtifactVersion("2.0.0"));
        assertEquals(1, delta.getMajor());
        assertEquals(0, delta.getMinor());
        assertEquals(0, delta.getPatch());
        
        delta.accumulateDelta(new DefaultArtifactVersion("2.0.0"), new DefaultArtifactVersion("2.0.1"));
        assertEquals(1, delta.getMajor());
        assertEquals(0, delta.getMinor());
        assertEquals(1, delta.getPatch());
    }
    
    
    private void testDelta(String versionA, String versionB, int expectedDeltaMajor, int expectedDeltaMinor, int expectedDeltaPatch) {
        
        VersionDelta delta = VersionDelta.computeDelta(new DefaultArtifactVersion(versionA), new DefaultArtifactVersion(versionB));
        assertNotNull(delta);
        assertEquals(expectedDeltaMajor, delta.getMajor());
        assertEquals(expectedDeltaMinor, delta.getMinor());
        assertEquals(expectedDeltaPatch, delta.getPatch());
    }
    
}
