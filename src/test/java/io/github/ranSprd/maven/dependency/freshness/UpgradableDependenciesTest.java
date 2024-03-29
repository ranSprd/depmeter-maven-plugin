package io.github.ranSprd.maven.dependency.freshness;

import java.util.Map;
import io.github.ranSprd.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author ran
 */
public class UpgradableDependenciesTest {
    
    public UpgradableDependenciesTest() {
    }

    @Test
    public void testSomeMethod() {
        Map<Dependency, ArtifactVersions> deps = DependencyBuilder.start()
                .add("groupA", "a", "2", "1", "3")
                .add("groupB", "b", "4-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .add("groupC", "c", "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .add("groupD", "c", DependencyBuilder.versionRange("[1.0, 2.0]"), "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .getDependencies();
        
        UpgradableDependencies u = UpgradableDependencies.select(deps);
        assertNotNull(u.getAllDependencies());
        assertEquals(4, u.getAllDependencies().size());
        
        UpgradableDependency foo = u.getAllDependencies().get(0);
        
    }
    
}
