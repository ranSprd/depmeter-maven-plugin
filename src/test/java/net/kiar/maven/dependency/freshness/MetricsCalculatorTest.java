package net.kiar.maven.dependency.freshness;

import java.util.Map;
import net.kiar.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class MetricsCalculatorTest {
    
    @Test
    public void testDrift() {
        Map<Dependency, ArtifactVersions> deps = DependencyBuilder.start()
                .add("groupA", "a", "2", "1", "3")
                .add("groupB", "b", "4-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .add("groupC", "c", "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .add("groupD", "c", DependencyBuilder.versionRange("[1.0, 2.0]"), "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .getDependencies();
        
        UpgradableDependencies u = UpgradableDependencies.select(deps);
        MetricsCalculator metricsCalculator = MetricsCalculator.get( u);
        System.out.println("drift score : "+metricsCalculator.overallDriftScore());
        
        assertEquals(2.005, metricsCalculator.overallDriftScore(), 0.0);
        assertEquals(2.005, metricsCalculator.packageDriftScore(), 0.0);
        
    }
    
}
