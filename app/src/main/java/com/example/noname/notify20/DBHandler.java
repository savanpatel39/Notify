package com.example.noname.notify20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Savan on 16-03-12.
 */
public class DBHandler extends SQLiteOpenHelper
{
    private Context context;
    private static final String DATABASE_NAME = "Notify.db";
    private static final int DATABASE_VERSION = 1;

    //category_details
    private static final String TABLE_CATEGORY_DETAILS = "category_details";
    private static final String COLUMN_CID = "cid";
    //private static final String COLUMN_CATNAME = "catname";

    //image_details
    private static final String TABLE_IMAGE_DETAILS = "image_details";
    private static final String COLUMN_IID = "iid";
    // for recording and images path
    private static final String COLUMN_PATH = "path";

    //recording_details
    private static final String TABLE_RECORDING_DETAILS = "recording_details";
    private static final String COLUMN_RID = "rid";

    //note_details
    private static final String TABLE_NOTE_DETAILS = "note_details";
    //for all tables except category_details
    private static final String COLUMN_NID = "nid";
    private static final String COLUMN_NTITLE = "ntitle";
    private static final String COLUMN_NCONTENT = "ncontent";
    //common for category name and note table
    private static final String COLUMN_CATNAME = "catname";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LON = "longitude";
    private static final String COLUMN_LAT = "latitude";
    private String query;

    public SQLiteDatabase db;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        query = " CREATE TABLE " +  TABLE_CATEGORY_DETAILS + "(" + COLUMN_CID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COLUMN_CATNAME + " TEXT);";
        db.execSQL(query);

        query = " CREATE TABLE " +  TABLE_NOTE_DETAILS + "(" + COLUMN_NID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COLUMN_NTITLE + " TEXT ," + COLUMN_NCONTENT + " TEXT ," + COLUMN_CID + " INTEGER ," + COLUMN_DATE + " TEXT ," + COLUMN_LON + " TEXT ," +  COLUMN_LAT + " TEXT, " +" FOREIGN KEY("+COLUMN_CID+") REFERENCES cat_details("+COLUMN_CID+"))";
        db.execSQL(query);

        query = "CREATE TABLE "+ TABLE_IMAGE_DETAILS + "(" + COLUMN_IID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COLUMN_NID + " INTEGER, " + COLUMN_PATH + " TEXT,FOREIGN KEY("+COLUMN_NID+") REFERENCES cat_details("+COLUMN_NID+"))";
        db.execSQL(query);

        query = "CREATE TABLE "+ TABLE_RECORDING_DETAILS + "(" + COLUMN_RID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COLUMN_NID + " INTEGER, " + COLUMN_PATH + " TEXT,FOREIGN KEY("+COLUMN_NID+") REFERENCES cat_details("+COLUMN_NID+"))";
        db.execSQL(query);

        String[] cat_array = {"Default","Personal","Work","Study","Travelling"};
        ContentValues values = new ContentValues();

        for (int i=0; i<5;i++)
        {
            values.put(COLUMN_CATNAME, cat_array[i]);
            db.insert(TABLE_CATEGORY_DETAILS, null, values);
        }
        values.clear();

//        values.put(COLUMN_NTITLE, "Sample Title");
//        values.put(COLUMN_CID, 1);
//        values.put(COLUMN_NCONTENT, "Your Text will be shown here!!!");
//        values.put(COLUMN_DATE, "03-13-2016");
//        values.put(COLUMN_LAT,"43.7706970");
//        values.put(COLUMN_LON,"-79.1716170");
//        db.insert(TABLE_NOTE_DETAILS, null, values);
//        values.clear();
//        db.close();
//        insertDefaultCategories();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

//    public void insertDefaultCategories()
//    {
//        Log.i("Any","Inserting Category");
//
//
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        for (int i=0; i<5;i++)
//        {
//            values.put(COLUMN_CATNAME, cat_array[i]);
//            db.insert(TABLE_CATEGORY_DETAILS, null, values);
//        }
//        db.close();
//    }

    public List<Category> getCategoryNames()
    {
        List<Category> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        query = "SELECT * FROM " + TABLE_CATEGORY_DETAILS;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            Category current = new Category(c.getInt(c.getColumnIndex(COLUMN_CID)),c.getString(c.getColumnIndex(COLUMN_CATNAME)));

            data.add(current);
            c.moveToNext();
        }

        c.close();
        db.close();
        return data;
    }

    public void insertCategory(String catname)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CATNAME, catname);
        db.insert(TABLE_CATEGORY_DETAILS, null, values);
        db.close();
    }

    public List<Note> getNotes(int cid)
    {
        List<Note> data = new ArrayList<>();//Collections.emptyList();

        SQLiteDatabase db = getWritableDatabase();
//        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_CID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , "+ COLUMN_CATNAME + " , " + COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" n LEFT JOIN "+ TABLE_CATEGORY_DETAILS +" c ON n."+ COLUMN_CID +" = c."+ COLUMN_CID +" WHERE n."+ COLUMN_CID  +" = "+ nid +";";
        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_CID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , " + COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" WHERE "+ COLUMN_CID  +" = "+ cid +";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_NID));
            String title = c.getString(c.getColumnIndex(COLUMN_NTITLE));
           // int cid = c.getInt(c.getColumnIndex(COLUMN_CID));
            String content = c.getString(c.getColumnIndex(COLUMN_NCONTENT));
            //Log.i("Any","Content "+content);
            String date = c.getString(c.getColumnIndex(COLUMN_DATE));
            data.add(new Note(id,cid,title,content,date));
            c.moveToNext();
        }
        c.close();
        db.close();
        return data;
    }

    public Note getNoteData(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
//        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , "+ COLUMN_CATNAME + "," + COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" n LEFT JOIN "+ TABLE_CATEGORY_DETAILS +" c ON n."+ COLUMN_CID +" = c."+ COLUMN_CID +" WHERE n."+ COLUMN_CID  +" = "+ id +";";
        query = "SELECT * FROM "+ TABLE_NOTE_DETAILS +" WHERE "+ COLUMN_NID  +" = "+ id +";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        Log.i("Nid","GetNoteData() : "+c.getInt(c.getColumnIndex(COLUMN_NID)) );
        int nid = c.getInt(c.getColumnIndex(COLUMN_NID));
        int cid = c.getInt(c.getColumnIndex(COLUMN_CID));
        String title = c.getString(c.getColumnIndex(COLUMN_NTITLE));
        String content = c.getString(c.getColumnIndex(COLUMN_NCONTENT));
        String date = c.getString(c.getColumnIndex(COLUMN_DATE));
        String lat = c.getString(c.getColumnIndex(COLUMN_LAT));
        String lon = c.getString(c.getColumnIndex(COLUMN_LON)) ;
//        String catname = c.getString(c.getColumnIndex(COLUMN_CATNAME));
        c.close();
        db.close();
        return new Note(nid,cid,title,content,date,lon,lat);
    }

    public void insertIntoNotes(Note n) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NTITLE, n.getNtitle());
        values.put(COLUMN_CID, n.getCid() );
        values.put(COLUMN_NCONTENT,n.getNcontent());
        values.put(COLUMN_DATE, n.getNdate());
        values.put(COLUMN_LAT,n.getNlat());
        values.put(COLUMN_LON, n.getNlon());
        Log.i("Save", "In insert Func");
        db.insert(TABLE_NOTE_DETAILS, null, values);
        values.clear();
        db.close();
    }

    public void updateNote(Note n) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NTITLE, n.getNtitle());
        //values.put(COLUMN_CID, n.getCid() );
        values.put(COLUMN_NCONTENT,n.getNcontent());
        //values.put(COLUMN_LAT,n.getNlat());
        //values.put(COLUMN_LON,n.getNlon());
        //problem is here!!!!
        db.update(TABLE_NOTE_DETAILS, values, "" + COLUMN_NID + "=?", new String[]{"" + n.getNid()});
        values.clear();
        db.close();
    }

    public int getLastCategoryId()
    {
        SQLiteDatabase db = getWritableDatabase();

        query = "SELECT "+ COLUMN_CID +" FROM " + TABLE_CATEGORY_DETAILS;

        Cursor c = db.rawQuery(query, null);
        c.moveToLast();

        int temp = c.getInt(c.getColumnIndex(COLUMN_CID));

        c.close();
        db.close();
        return temp;
    }

    public List<Note> getNotesAscByDate(int cid)
    {
        List<Note> data = new ArrayList<>();//Collections.emptyList();

        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_CID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , " + COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" WHERE " + COLUMN_CID  +" = "+ cid +" ORDER BY "+ COLUMN_DATE +" ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_NID));
            String title = c.getString(c.getColumnIndex(COLUMN_NTITLE));
            String content = c.getString(c.getColumnIndex(COLUMN_NCONTENT));
//            int cid = c.getInt(c.getColumnIndex(COLUMN_CID));
            //Log.i("Any","Content "+content);
            String date = c.getString(c.getColumnIndex(COLUMN_DATE));
            data.add(new Note(id,cid,title,content,date));
            c.moveToNext();
        }
        c.close();
        db.close();
        return data;
    }

    public List<Note> getNotesDescByDate(int cid)
    {
        List<Note> data = new ArrayList<>();//Collections.emptyList();

        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_CID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , "+ COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" WHERE " + COLUMN_CID  +" = "+ cid +" ORDER BY "+ COLUMN_DATE +" DESC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_NID));
            String title = c.getString(c.getColumnIndex(COLUMN_NTITLE));
            String content = c.getString(c.getColumnIndex(COLUMN_NCONTENT));
//            int cid = c.getInt(c.getColumnIndex(COLUMN_CID));
            //Log.i("Any","Content "+content);
            String date = c.getString(c.getColumnIndex(COLUMN_DATE));
            data.add(new Note(id,cid,title,content,date));
            c.moveToNext();
        }
        c.close();
        db.close();
        return data;
    }

    public List<Note> getNotesAscByTitle(int cid)
    {
        List<Note> data = new ArrayList<>();//Collections.emptyList();

        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_CID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , "+ COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" WHERE " + COLUMN_CID  +" = "+ cid +" ORDER BY "+ COLUMN_NTITLE +" ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_NID));
            String title = c.getString(c.getColumnIndex(COLUMN_NTITLE));
            String content = c.getString(c.getColumnIndex(COLUMN_NCONTENT));
//            int cid = c.getInt(c.getColumnIndex(COLUMN_CID));
            //Log.i("Any","Content "+content);
            String date = c.getString(c.getColumnIndex(COLUMN_DATE));
            data.add(new Note(id,cid,title,content,date));
            c.moveToNext();
        }
        c.close();
        db.close();
        return data;
    }

    public List<Note> getNotesDescByTitle(int cid)
    {
        List<Note> data = new ArrayList<>();//Collections.emptyList();

        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_CID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , " + COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" WHERE " + COLUMN_CID  +" = "+ cid +" ORDER BY "+ COLUMN_NTITLE +" DESC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_NID));
            String title = c.getString(c.getColumnIndex(COLUMN_NTITLE));
            String content = c.getString(c.getColumnIndex(COLUMN_NCONTENT));
//            int cid = c.getInt(c.getColumnIndex(COLUMN_CID));
            //Log.i("Any","Content "+content);
            String date = c.getString(c.getColumnIndex(COLUMN_DATE));
            data.add(new Note(id,cid,title,content,date));
            c.moveToNext();
        }
        c.close();
        db.close();
        return data;
    }

    public void insertAudioIntoDatabase(int nid,String path)
    {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        values.put(COLUMN_NID, nid);
        values.put(COLUMN_PATH, path);

        db.insert(TABLE_RECORDING_DETAILS, null, values);

        values.clear();
        db.close();
    }

    public void insertImageIntoDatabase(int nid,String path)
    {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        values.put(COLUMN_NID, nid);
        values.put(COLUMN_PATH, path);

        db.insert(TABLE_IMAGE_DETAILS, null, values);

        values.clear();
        db.close();
    }

    public List<Category> searchAllCategories(String query)
    {
        List<Category> srcCat = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        query = "SELECT "+ COLUMN_CID +" , "+ COLUMN_CATNAME +"  FROM " + TABLE_CATEGORY_DETAILS + " WHERE "+ COLUMN_CATNAME +" LIKE '%"+query+"%'";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();


        while(!c.isAfterLast())
        {
            int tempCid = c.getInt(c.getColumnIndex(COLUMN_CID));
            String tempCatName = c.getString(c.getColumnIndex(COLUMN_CATNAME));
            Log.i("Src","CatName : "+tempCatName);
            srcCat.add(new Category(tempCid,tempCatName));
            c.moveToNext();
        }
        c.close();
        db.close();
        return srcCat;
    }

    public List<Note> searchAllNotes(String queryText)
    {
        List<Note> srcNotes = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        query = "SELECT "+ COLUMN_NID +" , "+ COLUMN_CID +" , "+ COLUMN_NTITLE +" , "+ COLUMN_NCONTENT +" , " + COLUMN_DATE +" FROM "+ TABLE_NOTE_DETAILS +" WHERE "+COLUMN_NTITLE + " LIKE '%" + queryText+"%' OR "+COLUMN_NCONTENT +" LIKE '%"+queryText+"%'";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_NID));
            String title = c.getString(c.getColumnIndex(COLUMN_NTITLE));
            String content = c.getString(c.getColumnIndex(COLUMN_NCONTENT));
            int cid = c.getInt(c.getColumnIndex(COLUMN_CID));
            //Log.i("Any","Content "+content);
            String date = c.getString(c.getColumnIndex(COLUMN_DATE));
            srcNotes.add(new Note(id,cid,title,content,date));
            c.moveToNext();
        }
        c.close();
        db.close();
        return srcNotes;
    }

    public int getLastNid() {
        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_NID +" FROM "+TABLE_NOTE_DETAILS;
        Cursor c = db.rawQuery(query,null);
        c.moveToLast();

        int tempNid = c.getInt(c.getColumnIndex(COLUMN_NID));
        c.close();
        db.close();
        return tempNid;
    }

    public ArrayList<String> getImagePathsFromDatabase(int nid)
    {
        ArrayList<String> imgPaths = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_PATH +" FROM "+ TABLE_IMAGE_DETAILS +" WHERE "+ COLUMN_NID +" = "+ nid +" ; ";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            String tempPath = c.getString(c.getColumnIndex(COLUMN_PATH));
            imgPaths.add(tempPath);
            c.moveToNext();
        }
        c.close();
        db.close();
        return imgPaths;
    }

    public ArrayList<String> getAudioPathsFromDatabase(int nid)
    {
        ArrayList<String> audioPaths = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_PATH +" FROM "+ TABLE_RECORDING_DETAILS +" WHERE "+ COLUMN_NID +" = "+ nid +" ; ";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            String tempPath = c.getString(c.getColumnIndex(COLUMN_PATH));
            audioPaths.add(tempPath);
            c.moveToNext();
        }
        c.close();
        db.close();
        return audioPaths;
    }

    public int findCategoryIdByCatName(String cat_name)
    {
        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_CID + " FROM "+ TABLE_CATEGORY_DETAILS +" WHERE "+ COLUMN_CATNAME +" = '"+ cat_name +"';";
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        int tempCid = c.getInt(c.getColumnIndex(COLUMN_CID));

        c.close();
        db.close();
        return  tempCid;
    }

    public List<Category> getCatAscByTitle()
    {
        List<Category> data = new ArrayList<>();//Collections.emptyList();

        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_CID +" , "+ COLUMN_CATNAME +" FROM "+ TABLE_CATEGORY_DETAILS +" ORDER BY "+ COLUMN_CATNAME +" ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_CID));
            String title = c.getString(c.getColumnIndex(COLUMN_CATNAME));
            data.add(new Category(id,title));
            c.moveToNext();
        }
        c.close();
        db.close();
        return data;
    }

    public List<Category> getCatDescByTitle()
    {
        List<Category> data = new ArrayList<>();//Collections.emptyList();

        SQLiteDatabase db = getWritableDatabase();
        query = "SELECT "+ COLUMN_CID +" , "+ COLUMN_CATNAME +" FROM "+ TABLE_CATEGORY_DETAILS +" ORDER BY "+ COLUMN_CATNAME +" DESC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int id = c.getInt(c.getColumnIndex(COLUMN_CID));
            String title = c.getString(c.getColumnIndex(COLUMN_CATNAME));
            data.add(new Category(id,title));
            c.moveToNext();
        }
        c.close();
        db.close();
        return data;
    }
}