package com.example.noname.notify20;

/**
 * Created by Savan on 16-03-10.
 */
public class Category
{
    int cid;
    String catname;
    int noteCount;
//    String notes[][];


    public Category()
    {
    }

    public Category(int cid, String catname) {
        this.cid = cid;
        this.catname = catname;
    }

    public int getNoteCount() {
        return noteCount;
    }

    public void setNoteCount(int noteCount) {
        this.noteCount = noteCount;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public int getCid() {

        return cid;
    }

    public String getCatname() {
        return catname;
    }
}
