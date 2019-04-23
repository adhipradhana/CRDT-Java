package itb.sister.crdt.models;

public class Operation {
    private CharInfo data;
    private String siteId;
    private boolean operationType;
    private int operationCount;

    public Operation() {
        this.data = new CharInfo();
        this.siteId = "";
        this.operationType = false;
        this.operationCount = 0;
    }

    public Operation(CharInfo data, String siteId, boolean operationType, int operationCount){
        this.data = data;
        this.siteId = siteId;
        this.operationType = operationType;
        this.operationCount = operationCount;
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
}
