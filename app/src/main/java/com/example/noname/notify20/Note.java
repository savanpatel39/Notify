package com.example.noname.notify20;

/**
 * Created by Savan on 16-03-13.
 */
public class Note
{
    int nid;
    String ntitle;
    String ncontent;
    int cid;
    String ndate;
    String nlon;
    String nlat;

    public Note(int nid,int cid, String ntitle, String ncontent, String ndate) {
        this.nid = nid;
        this.cid = cid;
        this.ntitle = ntitle;
        this.ncontent = ncontent;
        this.ndate = ndate;
    }

    public Note(int nid,int cid, String ntitle, String ncontent, String ndate, String nlon, String nlat) {
        this.nid = nid;
        this.cid = cid;
        this.ntitle = ntitle;
        this.ncontent = ncontent;
        this.ndate = ndate;
        this.nlon = nlon;
        this.nlat = nlat;
    }

    public int getNid() {
        return nid;
    }

    public String getNtitle() {
        return ntitle;
    }

    public String getNcontent() {
        return ncontent;
    }

    public int getCid() {
        return cid;
    }

    public String getNdate() {
        return ndate;
    }

    public String getNlon() {
        return nlon;
    }

    public String getNlat() {
        return nlat;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public void setNtitle(String ntitle) {
        this.ntitle = ntitle;
    }

    public void setNcontent(String ncontent) {
        this.ncontent = ncontent;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setNdate(String ndate) {
        this.ndate = ndate;
    }

    public void setNlon(String nlon) {
        this.nlon = nlon;
    }

    public void setNlat(String nlat) {
        this.nlat = nlat;
    }
}
