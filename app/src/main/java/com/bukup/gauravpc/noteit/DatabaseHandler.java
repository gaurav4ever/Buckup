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

    private static final String TABLE_NOTES = "notes";  // notes table
    private static final String KEY_ID = "id";
    private static final String note_date = "date";
    private static final String note_title = "title";
    private static final String note_data = "data";

    private static final String TABLE_DIARY = "diary";  // daily diary table
    private static final String DIARY_KEY_ID = "id";
    private static final String DIARY_note_date = "date";
    private static final String DIARY_note_title = "title";
    private static final String DIARY_note_data = "data";

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

        String CREATE_DAILY_DIARY_TABLE = "CREATE TABLE " + TABLE_DIARY + "("
                +DIARY_KEY_ID+" INTEGER PRIMARY KEY,"
                +DIARY_note_date + " TEXT,"
                +DIARY_note_title + " TEXT,"
                + DIARY_note_data + " TEXT" + ")";
        db.execSQL(CREATE_DAILY_DIARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop notes table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);

        //drop daily diary table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        onCreate(db);
    }

    //insert into notes table
    String addNote(notesModel notesModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(note_date,notesModel.getDate());
        contentValues.put(note_title, notesModel.getTitle());
        contentValues.put(note_data, notesModel.getData());
        // Inserting Row
        int id= (int) db.insert(TABLE_NOTES, null, contentValues); //insert
        db.close();
        return id+"";
    }
    //Insert into daily diary table
    public String addPage_diary(DiaryModel diaryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(note_date,diaryModel.getDate());
        contentValues.put(note_title, diaryModel.getTitle());
        contentValues.put(note_data, diaryModel.getData());

        Log.e("status", diaryModel.getData() + " " + diaryModel.getTitle());
        // Inserting Row
        int id= (int) db.insert(TABLE_DIARY, null, contentValues); //insert
        db.close();
        return id+"";
    }

    //select from notes table
    public ArrayList<notesModel> ViewNotes(String newNoteAtTop){
        ArrayList<notesModel> notesModelList=new ArrayList<notesModel>();
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
            }while(cursor.moveToNext());
        }
        db.close();
        return notesModelList;
    }
    //select from notes table
    public ArrayList<DiaryModel> ViewDiary(){
        ArrayList<DiaryModel> diaryModelArrayList=new ArrayList<DiaryModel>();
        String selectQuery="";
        selectQuery = "SELECT  * FROM " + TABLE_DIARY +" ORDER BY "+ KEY_ID +" ASC" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                DiaryModel diaryModel=new DiaryModel();
                diaryModel.setId(Integer.parseInt(cursor.getString(0)));
                diaryModel.setDate(cursor.getString(1));
                diaryModel.setTitle(cursor.getString(2));
                diaryModel.setData(cursor.getString(3));
                diaryModelArrayList.add(diaryModel);
            }while(cursor.moveToNext());
        }
        db.close();
        return diaryModelArrayList;
    }

    //update notes table
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
    //update daily diary table
    public void updatePage_diary(String id_val,String date,String title,String body) {
        Log.e("id", id_val);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIARY, KEY_ID + "=" + Integer.parseInt(id_val), null);

        ContentValues contentValues=new ContentValues();
        contentValues.put(note_date,date);
        contentValues.put(note_title,title);
        contentValues.put(note_data,body);
        // Inserting Row
        db.insert(TABLE_DIARY, null, contentValues);
        Log.e("status", "Updated");
        db.close();
    }

    //delete row from notes table
    public void deleteNote(int id_val){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + "=" + id_val, null);
        Log.e("status", "Note deleted");
        db.close();
    }
    //delete row from Daily diary table
    public void deletePage_diary(int id_val){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIARY, KEY_ID + "=" + id_val, null);
        Log.e("status", "Diary Page deleted");
        db.close();
    }

    //total count of row from notes table
    public int getCount(){
        String countQuery="SELECT * FROM "+TABLE_NOTES;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        return cursor.getCount();
    }
    //total count of row from Daily diary table
    public int getCountOfDiary(){
        String countQuery="SELECT * FROM "+TABLE_DIARY;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        return cursor.getCount();
    }
}