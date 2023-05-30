package io.github.ranSprd.maven.dependency.freshness;

import java.io.File;
import java.util.Map;
import io.github.ranSprd.maven.dependency.freshness.testhelper.DependencyBuilder;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.api.VersionsHelper;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DependencyFreshnessMojoTest {

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
        when(versionsHelper.lookupDependenciesUpdates(any(), eq(false), eq(false)))
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

}
