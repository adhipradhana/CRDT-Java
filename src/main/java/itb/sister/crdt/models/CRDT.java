package itb.sister.crdt.models;

import java.util.HashMap;
import java.util.Map;

public class CRDT {

    private String siteId;
    private Character value;
    private boolean operation;
    private int[] positions;
    private Map<String, Integer> versionVector = new HashMap<>();

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    public boolean isOperation() {
        return operation;
    }

    public void setOperation(boolean operation) {
        this.operation = operation;
    }

    public int[] getPositions() {
        return positions;
    }

    public void setPositions(int[] positions) {
        this.positions = positions;
    }

    public Map<String, Integer> getVersionVector() {
        return versionVector;
    }

    public void setVersionVector(Map<String, Integer> versionVector) {
        this.versionVector = versionVector;
    }

    public CRDT(String siteId, Character value, boolean operation, int[] positions, Map<String, Integer> versionVector) {
        this.siteId = siteId;
        this.value = value;
        this.operation = operation;
        this.positions = positions;
        this.versionVector = versionVector;
    }

    public CRDT() {
        this.siteId = "";
        this.value = 'A';
        this.operation = true;
        this.positions = new int[]{1};
    }

}
