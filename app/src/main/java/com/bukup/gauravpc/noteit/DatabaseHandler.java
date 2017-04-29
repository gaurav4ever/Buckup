package com.bukup.gauravpc.noteit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaurav pc on 29-Nov-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notesDB";  // Database Name
    private static final String TABLE_NOTES = "notes";  // Contacts table name

    private static final String KEY_ID = "id";
    private static final String note_date = "date";
    private static final String note_title = "title";
    private static final String note_data = "data";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                +KEY_ID+" INTEGER PRIMARY KEY,"
               +note_date + " TEXT,"
                +note_title + " TEXT,"
                + note_data + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    String addNote(notesModel notesModel){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(note_date,notesModel.getDate());
        contentValues.put(note_title,notesModel.getTitle());
        contentValues.put(note_data, notesModel.getData());

        // Inserting Row
        int id= (int) db.insert(TABLE_NOTES, null, contentValues);
        db.close();

        return id+"";
    }

    public List<notesModel> ViewNotes(String newNoteAtTop){

        List<notesModel> notesModelList=new ArrayList<notesModel>();
        String selectQuery="";
        if(newNoteAtTop.equals("true")){
            selectQuery = "SELECT  * FROM " + TABLE_NOTES +" ORDER BY "+ KEY_ID +" DESC";
        }
        else{
            selectQuery = "SELECT  * FROM " + TABLE_NOTES +" ORDER BY "+ KEY_ID +" ASC" ;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{

                notesModel nm=new notesModel();
                nm.setId(Integer.parseInt(cursor.getString(0)));
                nm.setDate(cursor.getString(1));
                nm.setTitle(cursor.getString(2));
                nm.setData(cursor.getString(3));
                notesModelList.add(nm);

//                Log.e("status",cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2));

            }while(cursor.moveToNext());
        }
        db.close();

        return notesModelList;
    }

    public void updateNote(String id_val,String date,String title,String body) {

        Log.e("id", id_val);

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NOTES, KEY_ID + "=" + Integer.parseInt(id_val), null);


        ContentValues contentValues=new ContentValues();
        contentValues.put(note_date,date);
        contentValues.put(note_title,title);
        contentValues.put(note_data,body);
        // Inserting Row
        db.insert(TABLE_NOTES, null, contentValues);
        Log.e("status", "Updated");
        db.close();
    }

    public void deletNote(int id_val){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NOTES, KEY_ID + "=" + id_val, null);
        Log.e("status", "Note deleted");
        db.close();
    }

    public int getCount(){
        String countQuery="SELECT * FROM "+TABLE_NOTES;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        return cursor.getCount();
    }
}