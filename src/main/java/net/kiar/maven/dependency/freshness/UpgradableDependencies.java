package net.kiar.maven.dependency.freshness;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;

/**
 *
 * @author ran
 */
public class UpgradableDependencies {
    
    
    public static UpgradableDependencies select(Map<Dependency, ArtifactVersions> dependencies) {
        
        dependencies.entrySet().stream()
                .map(entry -> UpgradableDependency.create(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        
        
        
        return new UpgradableDependencies();
    }

    
}
