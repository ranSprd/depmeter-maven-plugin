package io.github.ranSprd.maven.dependency.freshness.metric;

import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 *
 * Contains the delta of version (normally semantic versioning) as 
 * 3D (major, minor, patch) values.
 * @author ran
 */
public class VersionDelta {

    private int major;
    private int minor;
    private int patch;

    public VersionDelta() {
        this(0, 0, 0);
    }

    public VersionDelta(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    private VersionDelta(int deltaArray[]) {
        this(deltaArray[0], deltaArray[1], deltaArray[2]);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }
    
    public void accumulate(VersionDelta delta) {
        if (delta != null) {
            major += delta.major;
            minor += delta.minor;
            patch += delta.patch;
        }
    }

    public void accumulateDelta(ArtifactVersion a, ArtifactVersion b) {
        int deltaArray[] = computeDeltaArray(a, b);
        major += deltaArray[0];
        minor += deltaArray[1];
        patch += deltaArray[2];
    }

    @Override
    public String toString() {
        return "VersionDelta {" + "major=" + major + ", minor=" + minor + ", patch=" + patch + '}';
    }
    
    
    
    private static int[] computeDeltaArray(ArtifactVersion a, ArtifactVersion b) {
        int result[] = new int[]{0, 0, 0};
        if (a != null && b != null) {
            result[0] = Math.abs(b.getMajorVersion() - a.getMajorVersion());
            if (result[0] < 1) {
                result[1] = Math.abs(b.getMinorVersion()- a.getMinorVersion());
                if (result[1] < 1) {
                    result[2] = Math.abs(b.getIncrementalVersion()- a.getIncrementalVersion());
                }
            }
        }
        return result;
    }
    
    public static VersionDelta computeDelta(ArtifactVersion a, ArtifactVersion b) {
        return new VersionDelta( computeDeltaArray(a, b));
    }

}
