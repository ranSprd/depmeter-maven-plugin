package io.github.ranSprd.maven.dependency.freshness.testhelper;

import io.github.ranSprd.maven.dependency.freshness.UpgradableDependency;
import java.util.Map;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author ranSprd 
 */
public class DependencyBuilderTest {
    
    
    @Test
    public void testMocking() {
        Map<Dependency, ArtifactVersions> deps = DependencyBuilder.start()
                .add("groupA", "a", "2", "1", "3")
                .add("groupB", "b", "4-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .add("groupC", "c", "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .add("groupD", "c", DependencyBuilder.versionRange("[1.0, 2.0]"), "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .getDependencies();
        
        assertNotNull(deps);
        Map.Entry<Dependency, ArtifactVersions> groupB = deps.entrySet().stream()
                                                            .filter( entry -> entry.getKey().getGroupId().equals("groupB"))
                                                            .findAny()
                                                            .get();
        assertEquals("groupB", groupB.getValue().getGroupId());
        assertEquals("b", groupB.getValue().getArtifactId());
        assertEquals("4-SNAPSHOT", groupB.getValue().getCurrentVersion().toString());
        assertEquals(4, groupB.getValue().getVersions(true).length);
    }
    
    @Test
    public void testVersionRange() {
        VersionRange range = DependencyBuilder.versionRange("[1.0, 2.0]");        
        assertNotNull(range);
    }
    
    @Test
    public void testUpgradableDependency() {
        UpgradableDependency dependency1 = DependencyBuilder.constructUpgradableDependency(
                "1.0", "group", "artifact", "3-SNAPSHOT", "1", "2.1", "3", "2.0-SNAPSHOT");        
        
        assertNotNull(dependency1);
        assertEquals(1, dependency1.getUsedVersion().getMajorVersion());
        assertEquals(0, dependency1.getUsedVersion().getMinorVersion());
        
        UpgradableDependency dependency2 = DependencyBuilder.constructUpgradableDependency(
                "[1.0, 2.0]", "group", "artifact", "3-SNAPSHOT", "1", "1.1", "2.0.1", "3", "2.0-SNAPSHOT");        
        
        assertNotNull(dependency2);
    }
        
    
    
}
