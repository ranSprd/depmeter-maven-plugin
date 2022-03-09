package net.kiar.maven.dependency.freshness;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;

/**
 *
 * @author ran
 */
public class UpgradableDependencies {
    
    private final Map<String, List<UpgradableDependency>> allDependencies;
    
    public static UpgradableDependencies select(Map<Dependency, ArtifactVersions> dependencies) {
        
        Map<String, List<UpgradableDependency>> transformed = dependencies.entrySet().stream()
                .map(entry -> UpgradableDependency.create(entry.getKey(), entry.getValue()))
                .collect( Collectors.groupingBy( UpgradableDependency::getGroupId));
        
        return new UpgradableDependencies(transformed);
    }
    

    public UpgradableDependencies(Map<String, List<UpgradableDependency>> map) {
        this.allDependencies = map;
    }

    public Map<String, List<UpgradableDependency>> getAllDependencies() {
        return allDependencies;
    }
    
}
