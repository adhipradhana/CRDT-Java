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
    private String text;

    public CRDT(String siteId, VersionVector versionVector, ServerPeerNode serverPeerNode) {
        this.siteId = siteId;
        this.versionVector = versionVector;
        this.serverPeerNode = serverPeerNode;
        this.text = "";
    }

    public CRDT(String siteId, List<CharInfo> dataList, VersionVector versionVector, ServerPeerNode serverPeerNode) {
        this.siteId = siteId;
        this.dataList = dataList;
        this.versionVector = versionVector;
        this.serverPeerNode = serverPeerNode;
        this.text = "";
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

    public String getText() {
        return text;
    }

    public void handleLocalInsert(char value, int index) {
        versionVector.increment(siteId);

        CharInfo data = generateCharInfo(value, index);
        insertData(index, data);
        insertText(value, index);

        System.out.println(text);

        serverPeerNode.broadcastInsertion(data, versionVector.getVersion(data.getSiteId()));
    }

    public void handleLocalDelete(char value, int index) {
        versionVector.increment(siteId);

        CharInfo data = removeData(index);
        removeText(index);

        System.out.println(text);

        serverPeerNode.broadcastDeletion(data, versionVector.getVersion(data.getSiteId()));
    }

    public void handleRemoteInsert(CharInfo charInfo, String siteId) {
        int index = findInsertIndex(charInfo);
        versionVector.increment(siteId);
        System.out.println(index);

        insertData(index, charInfo);
        System.out.println(charInfo.getValue());
        insertText(charInfo.getValue(), index);
    }

    public void handleRemoteDelete(CharInfo val, String siteId) {
        int index = findIndexByPosition(val);
        versionVector.increment(siteId);
        dataList.remove(index);

        removeText(index);
    }

    public void insertText(char value, int index) {
        int len = text.length();
        char[] updatedArr = new char[len + 1];
        text.getChars(0, index, updatedArr, 0);
        updatedArr[index] = value;
        text.getChars(index, len, updatedArr, index + 1);

        text = new String(updatedArr);
    }

    public void removeText(int index) {
        String newString = text.substring(0, index) + text.substring(index + 1);

        text = newString;
    }


    public CharInfo removeData(int index) {
        CharInfo data = dataList.remove(index);

        return data;
    }

    public void insertData(int index, CharInfo data) {
        dataList.add(index, data);
    }

    public int findInsertIndex(CharInfo val) {
        int left = 0;
        int right = dataList.size() - 1;

        if (dataList.size() == 0 || val.compareTo(dataList.get(left)) < 1) {
            return left;
        } else if (val.compareTo(dataList.get(right)) > 0) {
            return dataList.size();
        }

        while (left + 1 < right) {
            int mid = (int) Math.floor(left + (right - left) / 2);
            int compareNum = val.compareTo(dataList.get(mid));

            if (compareNum == 0) {
                return mid;
            } else if (compareNum > 0) {
                left = mid;
            } else {
                right = mid;
            }
        }

        return val.compareTo(dataList.get(left)) == 0 ? left : right;
    }

    public List<Integer> generatePosBetween(List<Integer> pos1, List<Integer> pos2, List<Integer> newPos, int level) {
        int base = (int) Math.pow(2, level) * 32;
        char strategy = (((level+1) % 2) == 0) ? '-' : '+';

        int id1 = pos1.size() > 0 ? pos1.get(0) : 0;
        int id2 = pos2.size() > 0 ? pos2.get(0) : base;

        if(id2 - id1 > 1) {
            int newDigit = generateIdBetween(id1, id2, strategy);
            newPos.add(newDigit);
            return newPos;
        } else if(id2 - id1 == 1) {
            newPos.add(id1);
            List<Integer> tempPos = new ArrayList<>(pos1);
            if (!tempPos.isEmpty()) {
                tempPos.remove(0);
            }

            return generatePosBetween(tempPos, new ArrayList<>(), newPos, level+1);
        } else {
            newPos.add(id1);
            List<Integer> tempPos1 = new ArrayList<>(pos1);

            if (!tempPos1.isEmpty()) {
                tempPos1.remove(0);
            }

            List<Integer> tempPos2 = new ArrayList<>(pos2);

            if (!tempPos2.isEmpty()) {
                tempPos2.remove(0);
            }

            return generatePosBetween(tempPos1, tempPos2, newPos, level+1);
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

        return (int) Math.floor(Math.random() * (max - min)) + min;
    }

    public int findIndexByPosition(CharInfo val) {
        int left = 0;
        int right = dataList.size() - 1;
        int mid, compareNum;

        if (dataList.size() == 0) {
            return -1;
        }

        while (left + 1 < right) {
            mid = (int) Math.floor(left + (right - left) / 2);
            compareNum = val.compareTo(dataList.get(mid));

            if (compareNum == 0) {
                return mid;
            } else if (compareNum > 0) {
                left = mid;
            } else {
                right = mid;
            }
        }

        if (val.compareTo(dataList.get(left)) == 0) {
            return left;
        } else if (val.compareTo(dataList.get(right)) == 0) {
            return right;
        } else {
            return -1;
        }
    }

}
