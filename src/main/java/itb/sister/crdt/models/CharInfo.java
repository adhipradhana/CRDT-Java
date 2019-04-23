package itb.sister.crdt.models;

public class CharInfo {
    private char value;
    private String siteId;
    private int[] positions;

    public CharInfo(){
        this.value = '\0';
        this.siteId = "";
        this.positions = new int[0];
    }

    public CharInfo(char value, String siteId,int[] positions) {
        this.value = value;
        this.siteId = siteId;
        this.positions = positions;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public int[] getPositions() {
        return positions;
    }

    public void setPositions(int[] positions) {
        this.positions = positions;
    }
}