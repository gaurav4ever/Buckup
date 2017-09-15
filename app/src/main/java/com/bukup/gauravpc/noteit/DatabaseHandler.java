package com.bukup.gauravpc.noteit;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.BucketList.CategoryItemModel;
import com.bukup.gauravpc.noteit.Models.BLModel;
import com.bukup.gauravpc.noteit.Models.DiaryModel;
import com.bukup.gauravpc.noteit.Models.ToDoModel;
import com.bukup.gauravpc.noteit.Models.notesModel;
import com.bukup.gauravpc.noteit.dailyDiary.PageDateModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gaurav pc on 29-Nov-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notesDB";  // Database Name
    private static final String SYNCED="isSynced";

    private static final String TABLE_NOTES = "notes";  // notes table
    private static final String KEY_ID = "id";
    private static final String note_created_on = "created_on";
    private static final String note_updated_on = "updated_on";
    private static final String note_title = "title";
    private static final String note_data = "data";
    private static final String note_tag = "tag";
    private static final String note_location = "location";

    private static final String TABLE_DIARY = "diary";  // daily diary table
    private static final String DIARY_KEY_ID = "id";
    private static final String DIARY_note_date = "date";
    private static final String DIARY_note_title = "title";
    private static final String DIARY_note_data = "data";

    private static final String TABLE_BUCKET_LIST = "bucket_list";  // Bucket List table
    private static final String BL_KEY_ID = "id";
    private static final String BL_TITLE = "title";
    private static final String BL_DESC = "desc";
    private static final String BL_CAT_ID = "cat_id";
    private static final String BL_TARGET_DATE = "start_date";
    private static final String BL_CREATED_ON = "created_on";
    private static final String BL_UPDATED_ON = "updated_on";

    private static final String TABLE_TODO_LIST = "ToDo_list";  // To Do list Table
    private static final String TD_KEY_ID = "id";
    private static final String TD_DESC = "desc";
    private static final String TD_IS_DONE = "isDone";
    private static final String TD_CREATED_ON = "created_on";
    private static final String TD_UPDATED_ON = "updated_on";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                +KEY_ID+" INTEGER PRIMARY KEY,"
               +note_created_on + " TEXT,"
                +note_updated_on + " TEXT,"
                +note_title + " TEXT,"
                + note_data + " TEXT,"
                + note_tag + " TEXT DEFAULT '0',"
                + note_location + " TEXT DEFAULT 'null',"
                +SYNCED + " TEXT DEFAULT '1'"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_DAILY_DIARY_TABLE = "CREATE TABLE " + TABLE_DIARY + "("
                +DIARY_KEY_ID+" INTEGER PRIMARY KEY,"
                +DIARY_note_date + " TEXT,"
                +DIARY_note_title + " TEXT,"
                + DIARY_note_data + " TEXT,"
                +SYNCED + " TEXT DEFAULT '1'"+")";
        db.execSQL(CREATE_DAILY_DIARY_TABLE);

        String CREATE_BUCKET_LIST_TABLE = "CREATE TABLE " + TABLE_BUCKET_LIST + "("
                +BL_KEY_ID+" INTEGER PRIMARY KEY,"
                +BL_TITLE + " TEXT,"
                +BL_DESC + " TEXT,"
                +BL_TARGET_DATE + " TEXT,"
                +BL_CREATED_ON + " TEXT,"
                +BL_UPDATED_ON + " TEXT,"
                + BL_CAT_ID + " INTEGER,"
                +SYNCED + " TEXT DEFAULT '1'"+")";
        db.execSQL(CREATE_BUCKET_LIST_TABLE);

        String CREATE_TODO_LIST_TABLE = "CREATE TABLE " + TABLE_TODO_LIST + "("
                +TD_KEY_ID+" INTEGER PRIMARY KEY,"
                +TD_DESC + " TEXT,"
                +TD_IS_DONE + " TEXT,"
                +TD_CREATED_ON + " TEXT,"
                +TD_UPDATED_ON + " TEXT,"
                +SYNCED + " TEXT DEFAULT '1'"+")";
        db.execSQL(CREATE_TODO_LIST_TABLE);
    }
    public void createTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }
    public void createToDoTable(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUCKET_LIST);


        String CREATE_BUCKET_LIST_TABLE = "CREATE TABLE " + TABLE_BUCKET_LIST + "("
                +BL_KEY_ID+" INTEGER PRIMARY KEY,"
                +BL_TITLE + " TEXT,"
                +BL_DESC + " TEXT,"
                +BL_TARGET_DATE + " TEXT,"
                +BL_CREATED_ON + " TEXT,"
                +BL_UPDATED_ON + " TEXT,"
                + BL_CAT_ID + " INTEGER" + ")";
        db.execSQL(CREATE_BUCKET_LIST_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop notes table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

        //drop daily diary table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);

        //drop Bucket List table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUCKET_LIST);

        onCreate(db);
    }
    public void dropCatTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS cat");
        onCreate(db);
    }

    //insert into notes table
    public String addNote(notesModel notesModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(note_created_on,notesModel.getCreated_on());
        contentValues.put(note_updated_on,notesModel.getUpdated_on());
        contentValues.put(note_title, notesModel.getTitle());
        contentValues.put(note_data, notesModel.getData());
        contentValues.put(note_location, notesModel.getLocation());
        contentValues.put(SYNCED, "0");

        // Inserting Row
        int id= (int) db.insert(TABLE_NOTES, null, contentValues); //insert
        db.close();
        return id+"";
    }
    public void setNoteTag(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE notes SET tag='1',isSynced='0' WHERE id=" + id);
    }
    public void removeNoteTag(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE notes SET tag='0',isSynced='0' WHERE id="+id);
    }
    public boolean hasTag(String tag){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery="SELECT * FROM notes WHERE id="+Integer.parseInt(tag);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            int check= Integer.parseInt(cursor.getString(5));
            if(check==1){
                return true;
            }
        }
        db.close();
        return false;
    }
    //Insert into daily diary table
    public String addPage_diary(DiaryModel diaryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DIARY_note_date,diaryModel.getDate());
        contentValues.put(DIARY_note_title, diaryModel.getTitle());
        contentValues.put(DIARY_note_data, diaryModel.getData());
        contentValues.put(SYNCED, "0");

        // Inserting Row
        int id= (int) db.insert(TABLE_DIARY, null, contentValues); //insert
        db.close();
        return id+"";
    }

    //Insert into Bucket List table
    public String addItem_bucketList(String title,String desc,String target_date,String cat_id,String created_on,String updated_on){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(BL_TITLE,title);
        contentValues.put(BL_DESC,desc);
        contentValues.put(BL_TARGET_DATE,target_date);
        contentValues.put(BL_CREATED_ON,created_on);
        contentValues.put(BL_UPDATED_ON,updated_on);
        contentValues.put(BL_CAT_ID,Integer.parseInt(cat_id));
        contentValues.put(SYNCED, "0");

        // Inserting Row
        int id= (int) db.insert(TABLE_BUCKET_LIST, null, contentValues); //insert
        db.close();
        return id+"";
    }
    //Insert into TO-DO List table
    public void addItem_TODOList(String desc,String created_on){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(TD_DESC,desc);
        contentValues.put(TD_CREATED_ON,created_on);
        contentValues.put(TD_UPDATED_ON,created_on);
        contentValues.put(TD_IS_DONE,"0");
        contentValues.put(SYNCED, "0");
        // Inserting Row
        db.insert(TABLE_TODO_LIST, null, contentValues); //insert
        db.close();
    }
    //select from notes table
    public ArrayList<notesModel> ViewNotes(){
        ArrayList<notesModel> notesModelList=new ArrayList<notesModel>();
        String selectQuery="";
        selectQuery = "SELECT  * FROM " + TABLE_NOTES +" ORDER BY date("+ note_updated_on +") DESC, id DESC" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                notesModel nm=new notesModel();
                nm.setId(Integer.parseInt(cursor.getString(0)));
                nm.setCreated_on(cursor.getString(1));
                nm.setUpdated_on(cursor.getString(2));
                nm.setTitle(cursor.getString(3));
                nm.setData(cursor.getString(4));
                nm.setTag(cursor.getString(5));
                nm.setLocation(cursor.getString(6));
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
        selectQuery = "SELECT  * FROM " + TABLE_DIARY +" ORDER BY date("+ DIARY_note_date +") DESC" ;
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
    public ArrayList<PageDateModel> ViewDiaryDates(){
        ArrayList<PageDateModel> pageDateModelArrayList=new ArrayList<PageDateModel>();
        String selectQuery="";
        selectQuery = "SELECT  * FROM " + TABLE_DIARY +" ORDER BY date("+ DIARY_note_date +") DESC" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                PageDateModel pageDateModel=new PageDateModel();
                pageDateModel.setId(cursor.getString(0));

                String date=cursor.getString(1);
                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                SimpleDateFormat day_of_week = new SimpleDateFormat("EEEE");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date fetchedDate = null;
                try {
                    fetchedDate = df.parse(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fetchedDate);
                    pageDateModel.setDay(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                    pageDateModel.setDay_ordinal(ordinal(cal.get(Calendar.DAY_OF_MONTH)));
                    pageDateModel.setMonth(month_date.format(cal.getTime()) + ", ");
                    pageDateModel.setYear(String.valueOf(cal.get(Calendar.YEAR)));
                    pageDateModel.setWeek_of_month(day_of_week.format(cal.getTime()) + "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                pageDateModelArrayList.add(pageDateModel);
            }while(cursor.moveToNext());
        }
        db.close();
        return pageDateModelArrayList;
    }
    public Cursor viewDiaryPage(String diaryPageId){
        String selectQuery = "SELECT  * FROM " + TABLE_DIARY +" WHERE "+ KEY_ID +" = "+diaryPageId ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    //select items from bucket list
    public ArrayList<BLModel> viewBL(){
        ArrayList<BLModel> blModelArrayList=new ArrayList<>();
        String selectQuery="";
        selectQuery = "SELECT  * FROM " + TABLE_BUCKET_LIST +" ORDER BY "+ KEY_ID +" DESC" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                BLModel blModel=new BLModel();
                blModel.setId(cursor.getString(0));
                blModel.setTitle(cursor.getString(1));
                blModel.setDesc(cursor.getString(2));
                blModel.setTarget_date(cursor.getString(3));
                blModel.setCreated_on(cursor.getString(4));
                blModel.setUpdated_on(cursor.getString(5));
                blModel.setCat_id("" + cursor.getString(6));

                //get Category values from CAT table
                String sql_cat="SELECT * FROM cat WHERE id = "+Integer.parseInt(cursor.getString(6));
                Cursor cat_cursor = db.rawQuery(sql_cat, null);
                if(cat_cursor.moveToFirst()){
                    do{
                        blModel.setCat_name(cat_cursor.getString(1));
                        blModel.setCat_detail(cat_cursor.getString(2));
                        blModel.setCat_img(Integer.parseInt(cat_cursor.getString(3)));
                    }while(cat_cursor.moveToNext());
                }

                blModelArrayList.add(blModel);
            }while(cursor.moveToNext());
        }
        db.close();
        return blModelArrayList;
    }
//    Select items from TO-DO list table
    public ArrayList<ToDoModel> viewTodo(){
        ArrayList<ToDoModel> TodoModelArrayList=new ArrayList<>();
        String selectQuery="";
        selectQuery = "SELECT  * FROM " + TABLE_TODO_LIST +" ORDER BY "+ KEY_ID +" DESC" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                ToDoModel todoModel=new ToDoModel();
                todoModel.setId(cursor.getString(0));
                todoModel.setDesc(cursor.getString(1));
                todoModel.setIsDone("" + cursor.getString(2));
                todoModel.setCreated_on(cursor.getString(3));
                todoModel.setUpdated_on(cursor.getString(4));
                TodoModelArrayList.add(todoModel);
            }while(cursor.moveToNext());
        }
        db.close();
        return TodoModelArrayList;
    }

    //update notes table
    public void updateNote(String id_val,String date,String title,String body,String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE notes SET updated_on = '"+date+"', title = '"+title+"', data='"+body+"', location='"+location+"', isSynced='0' WHERE id="+id_val;
        db.execSQL(sql);
        db.close();
    }
    //update daily diary table
    public void updatePage_diary(String id_val,String title,String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE diary SET title = '"+title+"', data='"+body+"', isSynced='0' WHERE id="+id_val;
        db.execSQL(sql);
        db.close();
    }

    //update bucket list item
    public void updateItem_bucketList(String id,String title,String desc,String target_date,String cat_id,String created_on,String updated_on){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUCKET_LIST, KEY_ID + "=" + Integer.parseInt(id), null);

        ContentValues contentValues=new ContentValues();
        contentValues.put(KEY_ID,Integer.parseInt(id));
        contentValues.put(BL_TITLE, title);
        contentValues.put(BL_DESC,desc);
        contentValues.put(BL_TARGET_DATE,target_date);
        contentValues.put(BL_CREATED_ON,created_on);
        contentValues.put(BL_UPDATED_ON,updated_on);
        contentValues.put(BL_CAT_ID, Integer.parseInt(cat_id));
        contentValues.put(SYNCED, "0");

        // Inserting Row
        db.insert(TABLE_BUCKET_LIST, null, contentValues); //insert
        db.close();
    }
    //delete row from notes table
    public void deleteNote(int id_val){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + "=" + id_val, null);
        db.close();
    }
    //delete row from Daily diary table
    public void deletePage_diary(int id_val){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIARY, KEY_ID + "=" + id_val, null);
        db.close();
    }
    //delete row from bucket list table
    public void deleteItemFromBucketList(int id_val){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUCKET_LIST, KEY_ID + "=" + id_val, null);
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
    //total count of row from Bucket List table
    public int getCountOfBucketList(){
        String countQuery="SELECT * FROM "+TABLE_BUCKET_LIST;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        return cursor.getCount();
    }
    //total count of row from TO-DO List table
    public int getCountOfToDoList(){
        String countQuery="SELECT * FROM "+TABLE_TODO_LIST;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        return cursor.getCount();
    }


    public int checkIfCatTableExist(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'cat' ", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return 1;
            }
            cursor.close();
        }
        return 0;
    }
    //insert category data into category table
    public void insertCat(String cat[],String cat_detail[],int cat_img[]){
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_CATEGORY_TABLE="CREATE TABLE cat (id INTEGER PRIMARY KEY,name TEXT,detail TEXT,img INTEGER)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        for(int i=0;i<cat.length;i++){
            ContentValues contentValues=new ContentValues();
            contentValues.put("name",cat[i]);
            contentValues.put("detail",cat_detail[i]);
            contentValues.put("img", cat_img[i]);
            // Inserting Row
            db.insert("cat", null, contentValues);
        }

        db.close();
    }
    public ArrayList<CategoryItemModel> getCatBucketList(){
        ArrayList<CategoryItemModel> categoryItemModelArrayList=new ArrayList<>();
        String selectQuery="";
        selectQuery = "SELECT  * FROM cat ORDER BY id ASC" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                CategoryItemModel categoryItemModel=new CategoryItemModel();
                categoryItemModel.setId(cursor.getString(0));
                categoryItemModel.setName(cursor.getString(1));
                categoryItemModel.setDetail(cursor.getString(2));
                categoryItemModel.setImg(Integer.parseInt(cursor.getString(3)));

                categoryItemModelArrayList.add(categoryItemModel);
            }while(cursor.moveToNext());
        }
        db.close();
        return categoryItemModelArrayList;
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                return suffixes[i % 10];

        }
    }

    public void alterTables(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql1="UPDATE notes SET isSynced='0' WHERE 1";
        String sql2="UPDATE diary SET isSynced='0' WHERE 1";
        String sql3="UPDATE bucket_list SET isSynced='0' WHERE 1";
//        String sql1="ALTER TABLE notes ADD COLUMN isSynced TEXT DEFAULT '0'";
//        String sql2="ALTER TABLE diary ADD COLUMN isSynced TEXT DEFAULT '0'";
//        String sql3="ALTER TABLE bucket_list ADD COLUMN isSynced TEXT DEFAULT '0'";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    /*
*****************************************************************************************************************
*****************************************************************************************************************
    BACKUP OPERATIONS @@@@@@@@@@@ FROM ANDROID DEVICE DATABASE TO ONLINE DATABASE
*****************************************************************************************************************
*****************************************************************************************************************
    */

    public Cursor getNotesData(){
        String selectQuery = "Select * from "+TABLE_NOTES+" WHERE isSynced = '0'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
    public Cursor getDiaryData(){
        String selectQuery = "Select * from "+TABLE_DIARY+" WHERE isSynced = '0'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
    public Cursor getBucketListData(){
        String selectQuery = "Select * from "+TABLE_BUCKET_LIST+" WHERE isSynced = '0'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    /*
*****************************************************************************************************************
*****************************************************************************************************************
    Restore Operation @@@@@@@@@@@ FROM ANDROID DEVICE DATABASE TO ONLINE DATABASE
*****************************************************************************************************************
*****************************************************************************************************************
    */

    public void truncateTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, null, null);
        db.delete(TABLE_DIARY, null, null);
        db.delete(TABLE_BUCKET_LIST, null, null);
    }


    /*
*****************************************************************************************************************
*****************************************************************************************************************
    Restore Operation @@@@@@@@@@@ FROM ANDROID DEVICE DATABASE TO ONLINE DATABASE
*****************************************************************************************************************
*****************************************************************************************************************
    */

    public void extra(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE notes ADD COLUMN tag TEXT DEFAULT '0'");
    }
}
