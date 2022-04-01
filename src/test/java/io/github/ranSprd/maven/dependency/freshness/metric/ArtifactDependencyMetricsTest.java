package io.github.ranSprd.maven.dependency.freshness.metric;

import io.github.ranSprd.maven.dependency.freshness.UpgradableDependency;
import io.github.ranSprd.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
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
                construct("[1.0, 2.0]", "group", "artifact", "3-SNAPSHOT", "1", "2.1", "3", "2-SNAPSHOT")
        );
        
        int nmbr = a.getVersionSequenceNumber();
//        System.out.println("sequence number " +nmbr);
        assertEquals(3, nmbr);
    }
    
    public static UpgradableDependency construct(String usedVersion, String groupId, String artifactId, String... versions) {
        Dependency dependency = DependencyBuilder.mockDependency(groupId, artifactId, usedVersion);
        ArtifactVersions artifactVersions = DependencyBuilder.mockVersions(groupId, artifactId, VersionRange.createFromVersion( usedVersion ), versions);
        
        return UpgradableDependency.create(dependency, artifactVersions);
    }
    
    
}
