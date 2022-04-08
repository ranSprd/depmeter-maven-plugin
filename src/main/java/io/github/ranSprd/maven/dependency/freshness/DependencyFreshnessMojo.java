package io.github.ranSprd.maven.dependency.freshness;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.model.*;
import org.codehaus.mojo.versions.utils.DependencyComparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.stream.XMLStreamException;
import io.github.ranSprd.maven.dependency.freshness.metric.ArtifactDependencyMetrics;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.versions.AbstractVersionsDisplayMojo;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.api.UpdateScope;
import org.codehaus.mojo.versions.api.VersionsHelper;
import org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader;
import org.codehaus.plexus.util.StringUtils;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "dependency-metrics", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true, requiresDirectInvocation = false, threadSafe = true)
public class DependencyFreshnessMojo extends AbstractVersionsDisplayMojo {

    /**
     * Whether to show additional information such as dependencies that do not need updating. Defaults to false.
     *
     */
    @Parameter( property = "verbose", defaultValue = "false" )
    private boolean verbose;
    

    /**
     * Our versions versionHelper.
     */
    private VersionsHelper versionsHelper;

    /**
     * The width to pad info messages.
     *
     */
    private static final int INFO_PAD_SIZE = 72;
    
    private static final String PACKAGE_STR = "               package : ";
    private static final String OVERALL_STR = "               overall : ";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        logInit();

        Set<Dependency> dependencies = new TreeSet<>(new DependencyComparator());
        dependencies.addAll(getProject().getDependencies());

        try {
            Map<Dependency, ArtifactVersions> updates = getHelper().lookupDependenciesUpdates(dependencies, false);
            UpgradableDependencies upgradable = UpgradableDependencies.select(updates);
            MetricsCalculator metricsCalculator = MetricsCalculator.get(upgradable);
            logMetricsToConsole(metricsCalculator);
        } catch (InvalidVersionSpecificationException | ArtifactMetadataRetrievalException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
    }

    @Override
    public VersionsHelper getHelper() throws MojoExecutionException {
        if (versionsHelper == null) {
            versionsHelper = super.getHelper(); 
        }
        return versionsHelper;
    }

    protected void setVersionsHelper(VersionsHelper versionsHelper) {
        this.versionsHelper = versionsHelper;
    }
    

    private void logMetricsToConsole(MetricsCalculator metricsCalculator) {
        String format = "0.000";
        NumberFormat doubleFormatter = new DecimalFormat(format);  
        
        if (verbose) {
            logLine(false, "Drift score values sorted by group and artifact");
            
            Map<String, List<ArtifactDependencyMetrics>> map = metricsCalculator.getMetricsByGroupId();
            for(Map.Entry<String, List<ArtifactDependencyMetrics>> artifactsOfSameGroup : map.entrySet()) {
                logLine(false, "  " +artifactsOfSameGroup.getKey());
                for(ArtifactDependencyMetrics entry : artifactsOfSameGroup.getValue()) {
                    String left = "    " +entry.getArtifactId() + " ";
                    String right = " " +doubleFormatter.format(entry.getDriftScore());
                    if (right.length() + left.length() + 3 > INFO_PAD_SIZE) {
                        logLine(false, left + "...");
                        logLine(false, StringUtils.leftPad(right, INFO_PAD_SIZE));

                    } else {
                        logLine(false, StringUtils.rightPad(left, INFO_PAD_SIZE - right.length(), ".") + right);
                    }
                    
                }
                
            }
        }
        logLine(false, "");
        logLine(false, "The following dependency freshness metrics for the entire project were calculated (zero = best value)");
        logLine(false, " drift score " );
        logLine(false, OVERALL_STR +doubleFormatter.format(metricsCalculator.overallDriftScore()) );
        logLine(false, PACKAGE_STR +doubleFormatter.format(metricsCalculator.packageDriftScore()) );
        logLine(false, " sequence number ");
        logLine(false, OVERALL_STR +metricsCalculator.overallVersionSequenceCount());
        logLine(false, PACKAGE_STR +metricsCalculator.packageVersionSequenceCount());
        logLine(false, " versing number delta ");
        logLine(false, OVERALL_STR +metricsCalculator.overallVersionDelta());
        logLine(false, PACKAGE_STR +"n/a");
    }
    
    /**
     * @param pom the pom to update.
     * @throws org.apache.maven.plugin.MojoExecutionException when things go wrong
     * @throws org.apache.maven.plugin.MojoFailureException when things go wrong in a very bad way
     * @throws javax.xml.stream.XMLStreamException when things go wrong with XML streaming
     * @see org.codehaus.mojo.versions.AbstractVersionsUpdaterMojo#update(org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader)
     * @since 1.0-alpha-1
     */
    @Override
    protected void update( ModifiedPomXMLEventReader pom )
        throws MojoExecutionException, MojoFailureException, XMLStreamException {
        // do nothing
    }
}
