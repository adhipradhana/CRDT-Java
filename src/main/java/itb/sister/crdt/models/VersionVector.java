package itb.sister.crdt.models;

import java.util.HashMap;
import java.util.Map;

public class VersionVector {
    private Map<String, Integer> version = new HashMap<>();

    public void increment(String siteId){
        version.put(siteId, version.get(siteId) + 1);
    }

    public int getVersion(String siteId){
        return version.get(siteId);
    }

    public void addSiteId(String siteId, int operationCount){
        version.put(siteId, operationCount);
    }

    public void removeSiteId(String siteId){
        version.remove(siteId);
    }
}
