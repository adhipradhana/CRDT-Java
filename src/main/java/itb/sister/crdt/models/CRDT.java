package itb.sister.crdt.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRDT {

    private String siteId;
    private List<CharInfo> dataList = new ArrayList<>();

    public CRDT(String siteId) {
        this.siteId = siteId;
    }

    public CRDT(String siteId, List<CharInfo> dataList) {
        this.siteId = siteId;
        this.dataList = dataList;
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

    
}
