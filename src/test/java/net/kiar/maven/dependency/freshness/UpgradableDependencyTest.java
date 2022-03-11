package net.kiar.maven.dependency.freshness;

import java.util.List;
import net.kiar.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ran
 */
public class UpgradableDependencyTest {
    
    @Test
    public void testIsUpgradableHappyCase() {
        UpgradableDependency underTest = construct("version", "group", "artifact", "1.0", "1.0.12", "2.3");
        assertNotNull(underTest);
        assertTrue(underTest.isUpgradable());
    }
    
    @Test
    public void testIsUpgradableIfLatestVersionIsUsed() {
        UpgradableDependency underTest = construct("2.3", "group", "artifact", "1.0", "1.0.12", "2.3");
        assertNotNull(underTest);
        assertFalse(underTest.isUpgradable());
    }
    
    @Test
    public void testIsUpgradableIfNoUsedVersionIsAvail() {
        UpgradableDependency underTest = constructWithoutCurrentVersion("group", "artifact", "1.0", "1.0.12", "2.3");
        assertNotNull(underTest);
        assertFalse(underTest.isUpgradable());
    }
    
    @Test
    public void testNewerVersionsHappyCase() {
        UpgradableDependency underTest = construct("1.0.1", "group", "artifact", "1.0", "1.0.12", "2.3");
        List<ArtifactVersion> newer = underTest.getAllNewerVersions();
        assertNotNull(newer);
        assertFalse(newer.isEmpty());
        assertEquals(2, newer.size());
    }
    
    @Test
    public void testNewerMajorVersionsHappyCase() {
        UpgradableDependency underTest = construct("1.0.1", "group", "artifact", "1.0", "1.0.12", "2.3", "2.4", "2.5", "3.0.1");
        List<ArtifactVersion> newer = underTest.getAllNewerMajorVersions();
        assertNotNull(newer);
        assertFalse(newer.isEmpty());
        assertEquals(4, newer.size());
    }
    
    
    public static UpgradableDependency construct(String usedVersion, String groupId, String artifactId, String... versions) {
        Dependency dependency = DependencyBuilder.mockDependency(groupId, artifactId, usedVersion);
        ArtifactVersions artifactVersions = DependencyBuilder.mockVersions(groupId, artifactId, VersionRange.createFromVersion( usedVersion ), versions);
        
        return UpgradableDependency.create(dependency, artifactVersions);
    }
    
    /** mock without any current version */
    public static UpgradableDependency constructWithoutCurrentVersion(String groupId, String artifactId, String... versions) {
        Dependency dependency = DependencyBuilder.mockDependency(groupId, artifactId, null);
        ArtifactVersions artifactVersions = DependencyBuilder.mockVersionsWithMissingCurrentVersion(groupId, artifactId, versions);
        
        return UpgradableDependency.create(dependency, artifactVersions);
    }
}
