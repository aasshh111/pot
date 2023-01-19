package com.example.test000111;

public class ModelCategory {

    String id, uid, potname, potid, potimage;
    long timestamp ;

    public ModelCategory() {

    }

    public ModelCategory(String id, String uid, String potname, String potid, String potimage, long timestamp) {
        this.id = id;
        this.uid = uid;
        this.potname = potname;
        this.potid = potid;
        this.timestamp = timestamp;
        this.potimage = potimage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPotname() { return potname; }

    public void setPotname(String potname) {
        this.potname = potname;
    }

    public String getPotid() {
        return potid;
    }

    public void setPotid(String potid) {
        this.potid = potid;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPotimage() {
        return potimage;
    }

    public void setPotimage(String potimage) {
        this.potimage = potimage;
    }
}
