package itb.sister.crdt.nodes;

import itb.sister.crdt.models.CharInfo;

import java.util.*;

public class CRDT {

    private String siteId;
    private List<CharInfo> dataList = new ArrayList<>();
    private VersionVector versionVector;

    public CRDT(String siteId, VersionVector versionVector) {
        this.siteId = siteId;
        this.versionVector = versionVector;
    }

    public CRDT(String siteId, List<CharInfo> dataList, VersionVector versionVector) {
        this.siteId = siteId;
        this.dataList = dataList;
        this.versionVector = versionVector;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }


    public List<CharInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<CharInfo> dataList) {
        this.dataList = dataList;
    }

    public void handleLocalInsert(char value, int index) {

    }

    public List<Integer> generatePosBetween(List<Integer> pos1, List<Integer> pos2, List<Integer> newPos, int level) {
        int base = (int) Math.pow(2, level) * 32;
        char strategy = ((level+1) % 2) == 0 ? '-' : '+';

        int id1 = pos1.size() > 0 ? pos1.get(0) : 0;
        int id2 = pos2.size() > 0 ? pos2.get(0) : base;

        if(id2 - id1 > 1) {
            int newDigit = generateIdBetween(id1, id2, strategy);
            newPos.add(newDigit);
            return newPos;
        } else if(id2 - id1 == 1) {
            newPos.add(id1);
            List<Integer> tempPos = new ArrayList<>(pos1);
            tempPos.remove(0);
            return generatePosBetween(tempPos, new ArrayList<>(), newPos, level+1);
        } else if (id1 == id2) {
            newPos.add(id1);
            List<Integer> tempPos1 = new ArrayList<>(pos1);
            tempPos1.remove(0);
            List<Integer> tempPos2 = new ArrayList<>(pos2);
            tempPos2.remove(0);
            return generatePosBetween(pos1, pos2, newPos, level+1);
        }
    }

    public CharInfo generateCharInfo(char value, int index) {
        List<Integer> posBefore;
        List<Integer> posAfter;

        try {
            int[] posBeforeInteger = dataList.get(index - 1).getPositions();
            posBefore = new ArrayList<Integer>(Arrays.asList(posBeforeInteger));
        } catch(Exception e) {
            posBefore = new ArrayList<Integer>();
        }

        try {
            posAfter = new ArrayList<Integer>(Arrays.asList(dataList.get(index).getPositions()));
        } catch(Exception e) {
            posAfter = new ArrayList<Integer>();
        }

        List<Integer> newPos = generatePosBetween(posBefore, posAfter, new ArrayList<Integer>(), 0);
        int[] arrNewPos = newPos.stream().mapToInt(Integer::intValue).toArray();
        return new CharInfo(value, this.siteId, arrNewPos);

    }

    public int generateIdBetween(int min, int max, char boundaryStrategy) {
        if((max - min) < 10) {
            min++;
        } else {
            if(boundaryStrategy == '-') {
                min = max - 10;
            } else {
                min++;
                max = min + 10;
            }
        }
        return (int)Math.floor(Math.random() * (max - min)) + min;
    }
}
