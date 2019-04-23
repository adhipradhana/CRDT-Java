package itb.sister.crdt.nodes;

import com.sun.security.ntlm.Server;
import itb.sister.crdt.models.CharInfo;

import java.util.*;
import java.util.stream.Collectors;

public class CRDT {

    private String siteId;
    private List<CharInfo> dataList = new ArrayList<>();
    private VersionVector versionVector;
    private ServerPeerNode serverPeerNode;

    public CRDT(String siteId, VersionVector versionVector, ServerPeerNode serverPeerNode) {
        this.siteId = siteId;
        this.versionVector = versionVector;
        this.serverPeerNode = serverPeerNode;
    }

    public CRDT(String siteId, List<CharInfo> dataList, VersionVector versionVector, ServerPeerNode serverPeerNode) {
        this.siteId = siteId;
        this.dataList = dataList;
        this.versionVector = versionVector;
        this.serverPeerNode = serverPeerNode;
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

    public void setServerPeerNode(ServerPeerNode serverPeerNode) {
        this.serverPeerNode = serverPeerNode;
    }

    public void handleLocalInsert(char value, int index) {
        versionVector.increment(siteId);

        CharInfo data = generateCharInfo(value, index);
        insertData(index, data);

        serverPeerNode.broadcastInsertion(data, versionVector.getVersion(data.getSiteId()));
    }

    public void handleLocalDelete(char value, int index) {
        versionVector.increment(siteId);

        CharInfo data = removeData(index);

        serverPeerNode.broadcastDeletion(data, versionVector.getVersion(data.getSiteId()));
    }


    public CharInfo removeData(int index) {
        CharInfo data = dataList.remove(index);

        return data;
    }

    public void insertData(int index, CharInfo data) {
        dataList.add(index, data);
    }

    public List<Integer> generatePosBetween(List<Integer> pos1, List<Integer> pos2, List<Integer> newPos, int level) {
        int base = (int) Math.pow(2, level) * 32;
        char strategy = ((level+1) % 2) == 0 ? '-' : '+';

        int id1 = pos1.size() > 0 ? pos1.get(0) : 0;
        int id2 = pos2.size() > 0 ? pos2.get(0) : base;


        if(id2 - id1 > 1) {
            System.out.println("memek");

            int newDigit = generateIdBetween(id1, id2, strategy);
            newPos.add(newDigit);

            System.out.println(newDigit);

            return newPos;
        } else if(id2 - id1 == 1) {
            System.out.println("ayaya");

            newPos.add(id1);
            List<Integer> tempPos = new ArrayList<>(pos1);
            tempPos.remove(0);
            return generatePosBetween(tempPos, new ArrayList<>(), newPos, level+1);
        } else {
            System.out.println("eyey");

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
            posBefore = Arrays.stream(posBeforeInteger).boxed().collect(Collectors.toList());
        } catch(Exception e) {
            posBefore = new ArrayList<Integer>();
        }

        try {
            int[] posAfterInteger = dataList.get(index).getPositions();
            posAfter = Arrays.stream(posAfterInteger).boxed().collect(Collectors.toList());
        } catch(Exception e) {
            posAfter = new ArrayList<Integer>();
        }

        System.out.println("Before ");
        for (int i = 0; i < posBefore.size(); i++) {
            System.out.println(posBefore.get(i));
        }

        System.out.println("After ");
        for (int i = 0; i < posAfter.size(); i++) {
            System.out.println(posAfter.get(i));
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
                System.out.println("kentu");
                System.out.println(min);
                System.out.println(max);
            } else {
                System.out.println(min);
                System.out.println(max);
                min++;
                max = min + 10;
                System.out.println("kponti");
                System.out.println(min);
                System.out.println(max);
            }
        }

        return (int) Math.floor((int) Math.random() * (max - min)) + min;
    }

}
