package net.kiar.maven.dependency.freshness.testhelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;
import org.codehaus.mojo.versions.ordering.VersionComparator;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author ran
 */
public class DependencyBuilder {

    private static final VersionComparator versionComparator = new MavenVersionComparator();

    private final Map<Dependency, ArtifactVersions> dependencies = new HashMap<>();

    public static DependencyBuilder start() {
        return new DependencyBuilder();
    }

    /**
     * 
     * @param groupId
     * @param artifactId
     * @param versions first given version is used as current version
     * @return 
     */
    public DependencyBuilder add(String groupId, String artifactId, String... versions) {
        if (versions == null || versions.length < 1) {
            dependencies.put(mockDependency(groupId, artifactId, "1"),
                    mockVersions(groupId, artifactId, "1"));
        } else {
            dependencies.put(mockDependency(groupId, artifactId, versions[0]),
                    mockVersions(groupId, artifactId, versions));
        }

        return this;
    }
    
    public DependencyBuilder add(String groupId, String artifactId, VersionRange versionRange, String... versions) {
        if (versions == null || versions.length < 1) {
            dependencies.put(mockDependency(groupId, artifactId, "1"),
                    mockVersions(groupId, artifactId, versionRange, "1"));
        } else {
            dependencies.put(mockDependency(groupId, artifactId, versions[0]),
                    mockVersions(groupId, artifactId, versionRange, versions));
        }

        return this;
    }

    public Map<Dependency, ArtifactVersions> getDependencies() {
        return dependencies;
    }

    public static Dependency mockDependency(String groupId, String artifactId, String version) {
        Dependency dep = mock(Dependency.class);

        // following mocks are not used and mockito complains
//        when(dep.getGroupId()).thenReturn(groupId);
//        when(dep.getArtifactId()).thenReturn(artifactId);
//        when(dep.getVersion()).thenReturn(version);
        return dep;
    }
        
    public static VersionRange versionRange(String versionOrRange) {
        VersionRange range = null;
        try {
            range = VersionRange.createFromVersionSpec(versionOrRange);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return range;
    }
    
    public static ArtifactVersions mockVersions(String groupId, String artifactId, String... versions) {
        return mockVersions(groupId, artifactId, versionRange(versions[0]), versions);
    }

    public static ArtifactVersions mockVersions(String groupId, String artifactId, VersionRange range, String... versions) {
        final Artifact artifact = new DefaultArtifact(
                groupId, artifactId, range, "foo", "bar",
                "jar", new DefaultArtifactHandler());

        return new ArtifactVersions(artifact, createArticArtifactVersions(versions), versionComparator);
    }
    
    public static ArtifactVersions mockVersionsWithMissingCurrentVersion(String groupId, String artifactId, String... versions) {
        final Artifact artifact = mock(Artifact.class);
    
        return new ArtifactVersions(artifact, createArticArtifactVersions(versions), versionComparator);
    }

    private static List<ArtifactVersion> createArticArtifactVersions(String... versions) {
        if (versions == null) {
            return List.of();
        }
        return Arrays.stream(versions)
                .map(version -> new DefaultArtifactVersion(version))
                .collect(Collectors.toList());
    }

}
