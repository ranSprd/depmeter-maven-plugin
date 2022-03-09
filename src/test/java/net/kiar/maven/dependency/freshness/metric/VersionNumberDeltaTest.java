package net.kiar.maven.dependency.freshness.metric;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class VersionNumberDeltaTest {
    
    @Test
    public void testComputeDelta() {
        testDelta("1.2.0", "1.2.0", 0, 0, 0);
        testDelta("", "1.2.0", 1, 0, 0);
        testDelta("2.0.1", "", 2, 0, 0);
    }
    
    @Test
    public void testAccumulate() {
        VersionNumberDelta delta = new VersionNumberDelta();
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
        
        VersionNumberDelta delta = VersionNumberDelta.computeDelta(new DefaultArtifactVersion(versionA), new DefaultArtifactVersion(versionB));
        assertNotNull(delta);
        assertEquals(expectedDeltaMajor, delta.getMajor());
        assertEquals(expectedDeltaMinor, delta.getMinor());
        assertEquals(expectedDeltaPatch, delta.getPatch());
    }
    
}
