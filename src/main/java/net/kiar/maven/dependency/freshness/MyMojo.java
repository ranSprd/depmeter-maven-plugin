package net.kiar.maven.dependency.freshness;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.model.*;
import org.codehaus.mojo.versions.utils.DependencyComparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.stream.XMLStreamException;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.versions.AbstractVersionsDisplayMojo;
import org.codehaus.mojo.versions.AbstractVersionsReport;
import org.codehaus.mojo.versions.AbstractVersionsUpdaterMojo;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.api.DefaultVersionsHelper;
import org.codehaus.mojo.versions.api.UpdateScope;
import org.codehaus.mojo.versions.api.VersionsHelper;
import org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.aether.impl.ArtifactResolver;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "dependency-metrics", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true, requiresDirectInvocation = false, threadSafe = true)
public class MyMojo extends AbstractVersionsDisplayMojo {

    /**
     * Whether to show additional information such as dependencies that do not need updating. Defaults to false.
     *
     */
    @Parameter( property = "verbose", defaultValue = "true" )
    private boolean verbose;
    
    /**
     * If specified then the display output will be sent to the specified file.
     *
     */
    @Parameter( property = "versions.outputFile" )
    private File outputFile;

    /**
     * Controls whether the display output is logged to the console.
     *
     */
    @Parameter( property = "versions.logOutput", defaultValue = "true" )
    private boolean logOutput;

    /**
     * The character encoding to use when writing to {@link #outputFile}.
     *
     */
    @Parameter( property = "outputEncoding", defaultValue = "${project.reporting.outputEncoding}" )
    private String outputEncoding;
    
    /**
     * Our versions helper.
     */
    private VersionsHelper helper;

    /**
     * The width to pad info messages.
     *
     */
    private static final int INFO_PAD_SIZE = 72;

    private boolean outputFileError = false;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        logInit();

        getLog().info("Hello, world.");

        Set<Dependency> dependencies = new TreeSet<>(new DependencyComparator());
        dependencies.addAll(project.getDependencies());

        try {
            logUpdates(getHelper().lookupDependenciesUpdates(dependencies, false), "Dependencies");
        } catch (InvalidVersionSpecificationException | ArtifactMetadataRetrievalException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
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

    protected void logLine(boolean error, String line) {
        if (logOutput) {
            if (error) {
                getLog().error(line);
            } else {
                getLog().info(line);
            }
        }
        if (outputFile != null && !outputFileError) {
            try {
                FileUtils.fileAppend(outputFile.getAbsolutePath(), outputEncoding,
                        error ? "> " + line + System.getProperty("line.separator")
                                : line + System.getProperty("line.separator"));
            } catch (IOException e) {
                getLog().error("Cannot send output to " + outputFile, e);
                outputFileError = true;
            }
        }
    }
    

    protected void logInit()
    {
        if ( outputFile != null && !outputFileError )
        {
            if ( outputFile.isFile() )
            {
                final String key = AbstractVersionsDisplayMojo.class.getName() + ".outputFile";
                String outputFileName;
                try
                {
                    outputFileName = outputFile.getCanonicalPath();
                }
                catch ( IOException e )
                {
                    outputFileName = outputFile.getAbsolutePath();
                }
                Set<String> files = (Set<String>) getPluginContext().get( key );
                if ( files == null )
                {
                    files = new LinkedHashSet<>();
                }
                else
                {
                    files = new LinkedHashSet<>( files );
                }
                if ( !files.contains( outputFileName ) )
                {
                    if ( !outputFile.delete() )
                    {
                        getLog().error( "Cannot delete " + outputFile + " will append instead" );
                    }
                }
                files.add( outputFileName );
                getPluginContext().put( key, files );
            }
            else
            {
                if ( outputFile.exists() )
                {
                    getLog().error( "Cannot send output to " + outputFile + " as it exists but is not a file" );
                    outputFileError = true;
                }
                else if ( !outputFile.getParentFile().isDirectory() )
                {
                    if ( !outputFile.getParentFile().mkdirs() )
                    {
                        outputFileError = true;
                    }
                }
            }
            if ( !outputFileError && org.apache.commons.lang3.StringUtils.isBlank( outputEncoding ) )
            {
                outputEncoding = System.getProperty( "file.encoding" );
                getLog().warn( "File encoding has not been set, using platform encoding " + outputEncoding
                    + ", i.e. build is platform dependent!" );
            }
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
        throws MojoExecutionException, MojoFailureException, XMLStreamException
    {
        // do nothing
    }
}
