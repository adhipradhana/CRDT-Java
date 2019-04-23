package itb.sister.crdt.models;

public class CharInfo implements Comparable<CharInfo> {
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

    @Override
    public int compareTo(CharInfo o) {
        int[] pos1 = this.positions;
        int[] pos2 = o.positions;

        for (int i = 0; i < Math.min(pos1.length, pos2.length); i++) {
            int id1 = pos1[i];
            int id2 = pos2[i];

            if (id1 < id2) {
                return -1;
            } else if (id1 > id2) {
                return 1;
            }
        }

        if (pos1.length < pos2.length) {
            return -1;
        } else if (pos1.length > pos2.length) {
            return 1;
        } else {
            return 0;
        }
    }
}