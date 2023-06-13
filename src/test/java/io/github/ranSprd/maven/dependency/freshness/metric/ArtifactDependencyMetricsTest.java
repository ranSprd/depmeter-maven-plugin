package io.github.ranSprd.maven.dependency.freshness.metric;

import io.github.ranSprd.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class ArtifactDependencyMetricsTest {
    
    @Test
    public void testSomeMethod() {
        ArtifactDependencyMetrics a = new ArtifactDependencyMetrics(
                DependencyBuilder.constructUpgradableDependency("[1.0, 2.0]", "group", "artifact", "3-SNAPSHOT", "1", "2.1", "3", "2.0-SNAPSHOT")
        );
        
        int nmbr = a.getVersionSequenceNumber();
//        System.out.println("sequence number " +nmbr);
        assertEquals(2, nmbr);
        assertEquals(2.0, a.getDriftScore(), 0.0001);
        
        assertEquals("group", a.getGroupId());
        assertEquals("artifact", a.getArtifactId());
    }
    
}
