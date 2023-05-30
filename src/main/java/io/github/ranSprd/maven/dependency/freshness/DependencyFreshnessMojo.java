package io.github.ranSprd.maven.dependency.freshness;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.model.*;
import org.codehaus.mojo.versions.utils.DependencyComparator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.stream.XMLStreamException;
import io.github.ranSprd.maven.dependency.freshness.metric.ArtifactDependencyMetrics;
import static java.util.Collections.emptySet;
import java.util.Objects;
import javax.inject.Inject;
import static org.apache.commons.lang3.StringUtils.countMatches;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.wagon.Wagon;
import org.codehaus.mojo.versions.AbstractVersionsDisplayMojo;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.api.VersionsHelper;
import org.codehaus.mojo.versions.api.recording.ChangeRecorder;
import static org.codehaus.mojo.versions.filtering.DependencyFilter.filterDependencies;
import org.codehaus.mojo.versions.filtering.WildcardMatcher;
import org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader;
import org.codehaus.plexus.util.StringUtils;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "dependency-metrics", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true, threadSafe = true)
public class DependencyFreshnessMojo extends AbstractVersionsDisplayMojo {

    /**
     * Whether to show additional information such as dependencies that do not need updating. Defaults to false.
     *
     */
    @Parameter( property = "verbose", defaultValue = "false" )
    private boolean verbose;
    
    
    /**
     * <p>Only take the specified <u>input</u> dependencies into account.</p>
     * <p><b><u>Note</u>: even if a version is specified, it will refer to the input dependency version.</b>
     * To filter <u>output</u> versions, please use {@link #ruleSet} or {@link #ignoredVersions}.</p>
     * <p>
     * Comma-separated list of extended GAV patterns.
     *
     * <p>
     * Extended GAV: groupId:artifactId:version:type:classifier:scope
     * </p>
     * <p>
     * The wildcard "*" can be used as the only, first, last or both characters in each token.
     * The version token does support version ranges.
     * </p>
     *
     * <p>
     * Example: {@code "mygroup:artifact:*,*:*:*:*:*:compile"}
     * </p>
     *
     * @since 2.12.0
     */
    @Parameter(property = "dependencyIncludes", defaultValue = WildcardMatcher.WILDCARD)
    private List<String> dependencyIncludes;

    /**
     * <p>Do not take the specified <u>input</u> dependencies into account.</p>
     * <p><b><u>Note</u>: even if a version is specified, it will refer to the input dependency version.</b>
     * To filter <u>output</u> versions, please use {@link #ruleSet} or {@link #ignoredVersions}.</p>
     * <p>
     * Comma-separated list of extended GAV patterns.
     *
     * <p>
     * Extended GAV: groupId:artifactId:version:type:classifier:scope
     * </p>
     * <p>
     * The wildca<u>Note:</u>rd "*" can be used as the only, first, last or both characters in each token.
     * The version token does support version ranges.
     * </p>
     *
     * <p>
     * Example: {@code "mygroup:artifact:*,*:*:*:*:*:provided,*:*:*:*:*:system"}
     * </p>
     *
     * @since 2.12.0
     */
    @Parameter(property = "dependencyExcludes")
    private List<String> dependencyExcludes;
    

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
    
    
    @Inject
    public DependencyFreshnessMojo(
            RepositorySystem repositorySystem,
            org.eclipse.aether.RepositorySystem aetherRepositorySystem,
            Map<String, Wagon> wagonMap,
            Map<String, ChangeRecorder> changeRecorders) {
        super(repositorySystem, aetherRepositorySystem, wagonMap, changeRecorders);
    }    

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        logInit();
        validateInput();
        
        Set<Dependency> dependencyManagement = emptySet();
        
        Set<Dependency> finalDependencyManagement = dependencyManagement;
        
        try {
            Map<Dependency, ArtifactVersions> updates = getHelper()
                .lookupDependenciesUpdates(
                        filterDependencies(
                                getProject().getDependencies().stream()
                                        .filter(dep -> finalDependencyManagement.stream()
                                                .noneMatch(depMan -> dependenciesMatch(dep, depMan)))
                                        .collect(
                                                () -> new TreeSet<>(DependencyComparator.INSTANCE),
                                                Set::add,
                                                Set::addAll),
                                dependencyIncludes,
                                dependencyExcludes,
                                "Dependencies",
                                getLog()),
                        false,
                        allowSnapshots);
            UpgradableDependencies upgradable = UpgradableDependencies.select(updates);
            MetricsCalculator metricsCalculator = MetricsCalculator.get(upgradable);
            logMetricsToConsole(metricsCalculator);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }




//        Set<Dependency> dependencies = new TreeSet<>(new DependencyComparator());
//        dependencies.addAll(getProject().getDependencies());
//
//        try {
//            Map<Dependency, ArtifactVersions> updates = getHelper().lookupDependenciesUpdates(dependencies, false);
//            UpgradableDependencies upgradable = UpgradableDependencies.select(updates);
//            MetricsCalculator metricsCalculator = MetricsCalculator.get(upgradable);
//            logMetricsToConsole(metricsCalculator);
//        } catch (Exception e) {
//            throw new MojoExecutionException(e.getMessage(), e);
//        }
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
    
    
    
    @Override
    protected void validateInput() throws MojoExecutionException {
        validateGAVList(dependencyIncludes, 6, "dependencyIncludes");
        validateGAVList(dependencyExcludes, 6, "dependencyExcludes");
    }
    
    
    /**
     * Validates a list of GAV strings (Command Line Parameters)
     * @param gavList list of the input GAV strings
     * @param numSections number of sections in the GAV to verify against
     * @param argumentName argument name to indicate in the exception
     * @throws MojoExecutionException if the argument is invalid
     */
    static void validateGAVList(List<String> gavList, int numSections, String argumentName)
            throws MojoExecutionException {
        if (gavList != null && gavList.stream().anyMatch(gav -> countMatches(gav, ":") >= numSections)) {
            throw new MojoExecutionException(argumentName + " should not contain more than 6 segments");
        }
    }
    
    
    // open for tests
    protected static boolean dependenciesMatch(Dependency dependency, Dependency managedDependency) {
        if (!managedDependency.getGroupId().equals(dependency.getGroupId())) {
            return false;
        }

        if (!managedDependency.getArtifactId().equals(dependency.getArtifactId())) {
            return false;
        }

        if (managedDependency.getScope() == null
                || Objects.equals(managedDependency.getScope(), dependency.getScope())) {
            return false;
        }

        if (managedDependency.getClassifier() == null
                || Objects.equals(managedDependency.getClassifier(), dependency.getClassifier())) {
            return false;
        }

        return dependency.getVersion() == null
                || managedDependency.getVersion() == null
                || Objects.equals(managedDependency.getVersion(), dependency.getVersion());
    }
    
}
