package itb.sister.crdt.models;

import java.util.ArrayList;

public class Version {

    private String siteId;
    private int counter;
    private ArrayList<Integer> exceptions = new ArrayList<>();

    public Version(String siteId) {
        this.siteId = siteId;
        this.counter = 0;
    }

    public Version(String siteId, int counter) {
        this.siteId = siteId;
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public ArrayList<Integer> getExceptions() {
        return exceptions;
    }

    public void setExceptions(ArrayList<Integer> exceptions) {
        this.exceptions = exceptions;
    }

    public void update (Version version) {
        int incomingCounter = version.getCounter();

        if (incomingCounter <= this.counter) {
            int index = this.exceptions.indexOf(incomingCounter);
            this.exceptions.remove(index);
        } else if (incomingCounter == this.counter + 1) {
            this.counter++;
        } else {
            for (int i = this.counter + 1; i < incomingCounter; i++) {
                this.exceptions.add(i);
            }

            this.counter = incomingCounter;
        }
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
