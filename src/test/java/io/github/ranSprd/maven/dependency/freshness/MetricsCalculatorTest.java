package io.github.ranSprd.maven.dependency.freshness;

import io.github.ranSprd.maven.dependency.freshness.metric.VersionDelta;
import java.util.Map;
import io.github.ranSprd.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class MetricsCalculatorTest {
    
    private static Map<Dependency, ArtifactVersions> TEST_SET1 = DependencyBuilder.start()
            .add("groupA", "a", "2", "1", "3")
            .add("groupB", "b", "4-SNAPSHOT", "1", "3", "2-SNAPSHOT")
            .add("groupC", "c", "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
            .add("groupD", "c", DependencyBuilder.versionRange("[1.0, 2.0]"), "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
            .getDependencies();
    
    private static Map<Dependency, ArtifactVersions> TEST_SET2 = DependencyBuilder.start()
            .add("groupA", "a", "1.0", "1.0.1", "2.1.1")
            .add("groupB", "b", "1.2.0", "1.2.1", "1.3.0", "1.0.1", "1.3.1")
            .getDependencies();
    
    @Test
    public void testDrift() {
        UpgradableDependencies u = UpgradableDependencies.select(TEST_SET1);
        MetricsCalculator metricsCalculator = MetricsCalculator.get( u);
//        System.out.println("drift score : "+metricsCalculator.overallDriftScore());
        
        assertEquals(2.005, metricsCalculator.overallDriftScore(), 0.0);
        assertEquals(2.005, metricsCalculator.packageDriftScore(), 0.0);
    }
    
    @Test
    public void testSequenceCount() {
        UpgradableDependencies u = UpgradableDependencies.select(TEST_SET1);
        MetricsCalculator metricsCalculator = MetricsCalculator.get( u);
//        System.out.println("sequence count : "+metricsCalculator.overallVersionSequenceCount());
        
        assertEquals(3, metricsCalculator.overallVersionSequenceCount());
        assertEquals(3, metricsCalculator.packageVersionSequenceCount());
    }
    
    @Test
    public void testDelta() {
        UpgradableDependencies u = UpgradableDependencies.select(TEST_SET2);
        MetricsCalculator metricsCalculator = MetricsCalculator.get( u);
//        System.out.println("version delta : "+metricsCalculator.overallVersionDelta());
        
        VersionDelta delta1 = metricsCalculator.overallVersionDelta();
        assertEquals(1, delta1.getMajor());
        assertEquals(1, delta1.getMinor());
        assertEquals(3, delta1.getPatch());
        
    }
    
}
