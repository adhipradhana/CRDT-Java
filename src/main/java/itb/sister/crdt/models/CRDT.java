package itb.sister.crdt.models;

public class CRDT {

    private String siteId;
    private Character value;
    private boolean operation;
    private int[] positions;

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

    public CRDT(String siteId, Character value, boolean operation, int[] positions) {
        this.siteId = siteId;
        this.value = value;
        this.operation = operation;
        this.positions = positions;
    }
}
