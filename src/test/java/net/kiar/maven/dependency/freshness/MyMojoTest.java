package net.kiar.maven.dependency.freshness;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;

public class MyMojoTest {

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
        File pom = new File("target/test-classes/project-to-test/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        MyMojo myMojo = (MyMojo) rule.lookupConfiguredMojo(pom, "dependency-metrics");
        assertNotNull(myMojo);
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
