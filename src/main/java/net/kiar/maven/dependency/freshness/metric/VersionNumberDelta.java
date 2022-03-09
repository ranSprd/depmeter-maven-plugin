package net.kiar.maven.dependency.freshness.metric;

import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 *
 * @author ran
 */
public class VersionNumberDelta {

    private int major;
    private int minor;
    private int patch;

    public VersionNumberDelta() {
        this(0, 0, 0);
    }

    public VersionNumberDelta(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    private VersionNumberDelta(int deltaArray[]) {
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

    public void accumulateDelta(ArtifactVersion a, ArtifactVersion b) {
        int deltaArray[] = computeDeltaArray(a, b);
        major += deltaArray[0];
        minor += deltaArray[1];
        patch += deltaArray[2];
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
    
    public static VersionNumberDelta computeDelta(ArtifactVersion a, ArtifactVersion b) {
        return new VersionNumberDelta( computeDeltaArray(a, b));
    }

}
