package net.kiar.maven.dependency.freshness;

import java.io.File;
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
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.api.VersionsHelper;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;
import org.codehaus.mojo.versions.ordering.VersionComparator;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DependencyFreshnessMojoTest {

    private static final VersionComparator versionComparator = new MavenVersionComparator();

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * @throws Exception if any
     */
    @Test
    public void testHappyCase() throws Exception {
//        File pom = new File("target/test-classes/project-to-test/");
        File pom = new File("src/test/resources/project-to-test/");
//        File pom = new File("src/test/resources/clean-pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        
        Map<Dependency, ArtifactVersions> deps = DependencyBuilder.start()
                .add("groupA", "a", "2", "1", "3")
                .add("groupB", "b", "4-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .add("groupC", "c", "3-SNAPSHOT", "1", "3", "2-SNAPSHOT")
                .getDependencies();
        
        VersionsHelper versionsHelper = mock(VersionsHelper.class);
        when(versionsHelper.lookupDependenciesUpdates(any(), eq(false)))
                            .thenReturn(deps);

        DependencyFreshnessMojo myMojo = (DependencyFreshnessMojo) rule.lookupConfiguredMojo(pom, "dependency-metrics");
        assertNotNull(myMojo);

        myMojo.setVersionsHelper(versionsHelper);
//        DependencyFreshnessMojo p = spy(myMojo);
//        when(p.getH)
        myMojo.execute();

//        File outputDirectory = ( File ) rule.getVariableValueFromObject( myMojo, "outputDirectory" );
//        assertNotNull( outputDirectory );
//        assertTrue( outputDirectory.exists() );
//
//        File touch = new File( outputDirectory, "touch.txt" );
//        assertTrue( touch.exists() );
    }

    /**
     * Do not need the MojoRule.
     */
    @WithoutMojo
    @Test
    public void testSomethingWhichDoesNotNeedTheMojoAndProbablyShouldBeExtractedIntoANewClassOfItsOwn() {
        assertTrue(true);
    }

    private static Dependency mockDependency(String groupId, String artifactId, String version) {
        Dependency dep = mock(Dependency.class);

//        when(dep.getGroupId()).thenReturn(groupId);
//        when(dep.getArtifactId()).thenReturn(artifactId);
//        when(dep.getVersion()).thenReturn(version);

        return dep;
    }
    
    private static ArtifactVersions mockVersions(String groupId, String artifactId, String... versions) {
        
        VersionRange range = null;
        try {
            range = VersionRange.createFromVersionSpec( versions[0] );
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Artifact artifact = new DefaultArtifact( 
                groupId, artifactId, range, "foo", "bar",
                                     "jar", new DefaultArtifactHandler() );
        
        return new ArtifactVersions(artifact, createArticArtifactVersions(versions), versionComparator);
    }
    
    private static List<ArtifactVersion> createArticArtifactVersions(String... versions){
        if (versions == null) {
            return List.of();
        }
        return Arrays.stream(versions)
                    .map(version -> new DefaultArtifactVersion(version))
                    .collect(Collectors.toList());
    }
    
    
    
    private static class DependencyBuilder {
        private final Map<Dependency, ArtifactVersions> dependencies = new HashMap<>();
        
        public static DependencyBuilder start() {
            return new DependencyBuilder();
        }
        
        public DependencyBuilder add(String groupId, String artifactId, String... versions) {
            if (versions == null || versions.length < 1) {
                dependencies.put( mockDependency(groupId, artifactId, "1"), 
                                mockVersions(groupId, artifactId,"1"));
            } else {
                dependencies.put( mockDependency(groupId, artifactId, versions[0]), 
                                mockVersions(groupId, artifactId, versions));
            }
            
            return this;
        }

        public Map<Dependency, ArtifactVersions> getDependencies() {
            return dependencies;
        }
        
        
    }

}
