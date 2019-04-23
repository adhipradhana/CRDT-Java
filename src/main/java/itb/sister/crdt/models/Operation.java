package itb.sister.crdt.models;

import itb.sister.crdt.nodes.VersionVector;

public class Operation {
    private CharInfo data;
    private boolean operationType;
    private Version version;

    public Operation() {
        this.data = new CharInfo();
        this.operationType = false;
        this.version = null;
    }

    public Operation(CharInfo data, boolean operationType, Version version){
        this.data = data;
        this.operationType = operationType;
        this.version = version;
    }

    public CharInfo getData() {
        return data;
    }

    public void setData(CharInfo data) {
        this.data = data;
    }

    public boolean isOperationType() {
        return operationType;
    }

    public void setOperationType(boolean operationType) {
        this.operationType = operationType;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
