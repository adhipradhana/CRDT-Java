package itb.sister.crdt.models;

import itb.sister.crdt.nodes.VersionVector;

public class Operation {
    private CharInfo data;
    private String siteId;
    private boolean operationType;
    private int operationCount;
    private VersionVector versionVector;

    public Operation() {
        this.data = new CharInfo();
        this.siteId = "";
        this.operationType = false;
        this.operationCount = 0;
        this.versionVector = null;
    }

    public Operation(CharInfo data, String siteId, boolean operationType, int operationCount, VersionVector versionVector){
        this.data = data;
        this.siteId = siteId;
        this.operationType = operationType;
        this.operationCount = operationCount;
        this.versionVector = versionVector;
    }

    public CharInfo getData() {
        return data;
    }

    public void setData(CharInfo data) {
        this.data = data;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public boolean isOperationType() {
        return operationType;
    }

    public void setOperationType(boolean operationType) {
        this.operationType = operationType;
    }

    public int getOperationCount() {
        return operationCount;
    }

    public void setOperationCount(int operationCount) {
        this.operationCount = operationCount;
    }

    public VersionVector getVersionVector() {
        return versionVector;
    }

    public void setVersionVector(VersionVector versionVector) {
        this.versionVector = versionVector;
    }
}
