package io.github.ranSprd.maven.dependency.freshness;

import java.util.List;
import java.util.Map;
import org.apache.maven.model.Dependency;
import org.codehaus.mojo.versions.api.ArtifactVersions;

/**
 *
 * @author ran
 */
public class UpgradableDependencies {
    
    private final List<UpgradableDependency> allDependencies;
    
    public static UpgradableDependencies select(Map<Dependency, ArtifactVersions> dependencies) {
        
        List<UpgradableDependency> transformed = dependencies.entrySet().stream()
                .map(entry -> UpgradableDependency.create(entry.getKey(), entry.getValue()))
                .toList();
        
        return new UpgradableDependencies(transformed);
    }
    

    public UpgradableDependencies(List<UpgradableDependency> list) {
        this.allDependencies = list;
    }

    public List<UpgradableDependency> getAllDependencies() {
        return allDependencies;
    }
    
}
