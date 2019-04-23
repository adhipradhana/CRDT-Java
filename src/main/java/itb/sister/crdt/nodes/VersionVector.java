package itb.sister.crdt.nodes;

import itb.sister.crdt.models.Version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VersionVector {

    private ArrayList<Version> versions = new ArrayList<>();
    private Version localVersion;

    public VersionVector(String siteId) {
        this.localVersion = new Version(siteId);
        this.versions.add(this.localVersion);
    }

    public void setLocalVersion(Version localVersion) {
        this.localVersion = localVersion;
    }

    public ArrayList<Version> getVersions() { return this.versions; }

    public void increment() {
        this.localVersion.setCounter(this.localVersion.getCounter() + 1);
    }

    public void update(Version incomingVersion) {

        Version existingVersion = this.getVersionFromVector(incomingVersion);

        if (existingVersion == null) {
            Version newVersion = new Version(incomingVersion.getSiteId());

            newVersion.update(incomingVersion);
            this.versions.add(newVersion);
        } else {
            existingVersion.update(incomingVersion);
        }
    }

    public boolean hasBeenApplied(Version incomingVersion) {
        Version localIncomingVersion = this.getVersionFromVector(incomingVersion);
        boolean isIncomingInVersionVector = (localIncomingVersion != null);

        if (!isIncomingInVersionVector) return false;

        boolean isIncomingLower = incomingVersion.getCounter() <= localIncomingVersion.getCounter();
        boolean isInExceptions = localIncomingVersion.getExceptions().contains(incomingVersion.getCounter());

        return isIncomingLower && !isInExceptions;
    }

    public Version getVersionFromVector(Version incomingVersion) {
        Version existingVersion = null;

        for (int i = 0; i < versions.size(); i++) {
            if (incomingVersion.getSiteId().equals(versions.get(i).getSiteId())) {
                existingVersion = versions.get(i);
            }

            System.out.println(incomingVersion.getSiteId());
        }

        return existingVersion;
    }

    public Version getLocalVersion() {
        return new Version(localVersion.getSiteId(), localVersion.getCounter());
    }
}
