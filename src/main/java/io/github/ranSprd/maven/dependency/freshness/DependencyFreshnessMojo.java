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
            
//            logUpdates(updates, "Dependencies");
        } catch (InvalidVersionSpecificationException | ArtifactMetadataRetrievalException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        
    }

    
    
//    @Override
//    protected VersionsHelper getHelper() throws MojoExecutionException {
//        if (versionHelper == null) {
//            versionHelper = super.getHelper();
//        }
//        return versionHelper;
//    }

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
        logLine(false, "               overall : " +doubleFormatter.format(metricsCalculator.overallDriftScore()) );
        logLine(false, "               package : " +doubleFormatter.format(metricsCalculator.packageDriftScore()) );
        logLine(false, " sequence number ");
        logLine(false, "               overall : " +metricsCalculator.overallVersionSequenceCount());
        logLine(false, "               package : " +metricsCalculator.packageVersionSequenceCount());
        logLine(false, " versing number delta ");
        logLine(false, "               overall : " +metricsCalculator.overallVersionDelta());
        logLine(false, "               package : " +"n/a");
    }
    
    private void logUpdates(Map<Dependency, ArtifactVersions> updates, String section) {
        List<String> withUpdates = new ArrayList<>();
        List<String> usingCurrent = new ArrayList<>();
        Iterator i = updates.values().iterator();
        while (i.hasNext()) {
            ArtifactVersions versions = (ArtifactVersions) i.next();
            String left = "  " + ArtifactUtils.versionlessKey(versions.getArtifact()) + " ";
            final String current;
            ArtifactVersion latest;
            if (versions.isCurrentVersionDefined()) {
                current = versions.getCurrentVersion().toString();
                latest = versions.getNewestUpdate(UpdateScope.ANY, allowSnapshots);
            } else {
                ArtifactVersion newestVersion
                        = versions.getNewestVersion(versions.getArtifact().getVersionRange(), allowSnapshots);
                current = versions.getArtifact().getVersionRange().toString();
                latest = newestVersion == null ? null
                        : versions.getNewestUpdate(newestVersion, UpdateScope.ANY, allowSnapshots);
                if (latest != null
                        && ArtifactVersions.isVersionInRange(latest, versions.getArtifact().getVersionRange())) {
                    latest = null;
                }
            }
            String right = " " + (latest == null ? current : current + " -> " + latest);
            List<String> t = latest == null ? usingCurrent : withUpdates;
            if (right.length() + left.length() + 3 > INFO_PAD_SIZE) {
                t.add(left + "...");
                t.add(StringUtils.leftPad(right, INFO_PAD_SIZE));

            } else {
                t.add(StringUtils.rightPad(left, INFO_PAD_SIZE - right.length(), ".") + right);
            }
        }

        if (verbose) {
            if (usingCurrent.isEmpty()) {
                if (!withUpdates.isEmpty()) {
                    logLine(false, "No dependencies in " + section + " are using the newest version.");
                    logLine(false, "");
                }
            } else {
                logLine(false, "The following dependencies in " + section + " are using the newest version:");
                i = usingCurrent.iterator();
                while (i.hasNext()) {
                    logLine(false, (String) i.next());
                }
                logLine(false, "");
            }
        }

        if (withUpdates.isEmpty()) {
            if (!usingCurrent.isEmpty()) {
                logLine(false, "No dependencies in " + section + " have newer versions.");
                logLine(false, "");
            }
        } else {
            logLine(false, "The following dependencies in " + section + " have newer versions:");
            i = withUpdates.iterator();
            while (i.hasNext()) {
                logLine(false, (String) i.next());
            }
            logLine(false, "");
        }
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
