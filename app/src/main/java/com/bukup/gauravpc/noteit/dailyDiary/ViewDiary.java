package com.bukup.gauravpc.noteit.dailyDiary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.Models.DiaryModel;
import com.bukup.gauravpc.noteit.Models.notesModel;
import com.bukup.gauravpc.noteit.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewDiary extends AppCompatActivity {

    DatabaseHandler db;
    ListView listView;
    ImageView goBackImage,calendarIcon;
    RelativeLayout emptyLayout;
    DiaryAdapter diaryAdapter;
    ArrayList<PageDateModel>pageDateModelArrayList;
    PageDateModel pageDateModel;
    GridView dateGridViewLayout;
    PageDateAdapter pageDateAdapter;
    Dialog dialog;

    int images[]={R.drawable.circle0,R.drawable.circle1,R.drawable.circle2,R.drawable.circle3,R.drawable.circle4,R.drawable.circle5,R.drawable.circle6,R.drawable.circle7,R.drawable.circle8,R.drawable.circle9,R.drawable.circle10};

    //Navigation Drawer Options
    LinearLayout notesLayout,dailyDiaryLayout,bucketListLayout,slamBookLayout,showListLayout,gridViewLayout,feedbackLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diary);

        goBackImage=(ImageView)findViewById(R.id.goBackImg);
        goBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        emptyLayout=(RelativeLayout)findViewById(R.id.emptyLayout);
        listView=(ListView)findViewById(R.id.list);

        //Database operations
        db = new DatabaseHandler(this);
        int count=db.getCountOfDiary();
        if(count==0){
            listView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            emptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ViewDiary.this,NewPage_Diary.class);
                    startActivityForResult(intent, 2);
                }
            });
        }
        else{
            ArrayList<DiaryModel> diaryModelArrayList=db.ViewDiary(); //get diary content form database
            diaryAdapter=new DiaryAdapter(getApplicationContext(),R.layout.row_daily_diary,diaryModelArrayList);
            listView.setAdapter(diaryAdapter);
            emptyLayout.setVisibility(View.GONE);
        }
        calendarIcon=(ImageView)findViewById(R.id.calendar_icon);
        dialog = new Dialog(ViewDiary.this);
        dialog.setContentView(R.layout.layout_calendar);
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateGridViewLayout = (GridView) dialog.findViewById(R.id.dateGridView);

                DatabaseHandler db = new DatabaseHandler(getApplication());
                pageDateModelArrayList=db.ViewDiaryDates(); //array list for searching of pages in diary
                pageDateAdapter = new PageDateAdapter(getApplicationContext(), R.layout.row_grid_date_of_page, pageDateModelArrayList);
                dateGridViewLayout.setAdapter(pageDateAdapter);

                ImageView crossImageView = (ImageView) dialog.findViewById(R.id.cross);
                crossImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
    public class DiaryAdapter extends ArrayAdapter{

        private LayoutInflater inflater;
        public ArrayList<DiaryModel>diaryModelList;
        private int resource;

        public DiaryAdapter(Context context, int resource,ArrayList<DiaryModel> notesModelList) {
            super(context, resource, notesModelList);
            this.diaryModelList = notesModelList;
            this.resource = resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return diaryModelList.size();
        }
        @Override
        public Object getItem(int position) {
            return diaryModelList.get(position);
        }
        @Override
        public int getViewTypeCount() {
            if(diaryModelList.size()==0){
                return 1;
            }
            return diaryModelList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder{
            TextView diaryPageText_id;
            TextView diaryPageText_day;
            TextView diaryPageText_day_ordinal;
            TextView diaryPageText_month;
            TextView diaryPageText_year;
            TextView diaryPageText_title;
            TextView diaryPageText_data;
            CardView noteCard;
            RelativeLayout circle_dateLayout;
            RelativeLayout editLayoutLayout;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int flag=0;
            ViewHolder viewHolder;
//            Log.e("pos",""+position);
            if(convertView==null && position==0){

//                DateFormat df = new SimpleDateFormat("dd/MM/yy");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String todayDate=df.format(date);
                if(diaryModelList.get(position).getDate().equals(todayDate)) {

                    convertView=inflater.inflate(R.layout.layout_latest_page_of_diary,null);
                    viewHolder=new ViewHolder();
                    viewHolder.diaryPageText_id=(TextView)convertView.findViewById(R.id.note_id);
                    viewHolder.diaryPageText_day=(TextView)convertView.findViewById(R.id.day);
                    viewHolder.diaryPageText_day_ordinal=(TextView)convertView.findViewById(R.id.ordinal);
                    viewHolder.diaryPageText_month=(TextView)convertView.findViewById(R.id.month);
                    viewHolder.diaryPageText_year=(TextView)convertView.findViewById(R.id.year);
                    viewHolder.diaryPageText_title=(TextView)convertView.findViewById(R.id.title);
                    viewHolder.diaryPageText_data=(TextView)convertView.findViewById(R.id.body);
                    viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
                    viewHolder.circle_dateLayout=(RelativeLayout)convertView.findViewById(R.id.circle_date);

                    viewHolder.diaryPageText_id.setText(""+diaryModelList.get(position).getId());viewHolder.diaryPageText_id.setVisibility(View.GONE);
                    //date operations
                    String day_now = null,day_now_ordinal= null,month_now= null,year_now= null;
                    String date_=null;
                    date_=""+diaryModelList.get(position).getDate();
                    df = new SimpleDateFormat("yyyy-MM-dd");
                    try {

                        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                        Date fetchedDate = df.parse(date_);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(fetchedDate);
                        day_now= ""+cal.get(Calendar.DAY_OF_MONTH);
                        day_now_ordinal=ordinal(cal.get(Calendar.DAY_OF_MONTH));
                        month_now = month_date.format(cal.getTime())+", ";
                        year_now= String.valueOf(cal.get(Calendar.YEAR));
                        viewHolder.diaryPageText_day.setText(day_now);
                        viewHolder.diaryPageText_day_ordinal.setText(day_now_ordinal);
                        viewHolder.diaryPageText_month.setText(month_now);
                        viewHolder.diaryPageText_year.setText(year_now);

                        //set color of the date layout background
                        if(position==0){
                            viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle2);
                        }
                        else if(Calendar.DAY_OF_MONTH %2==0){
                            viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle1);
                        }else{
                            viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    viewHolder.diaryPageText_title.setText(diaryModelList.get(position).getTitle());
                    String data=diaryModelList.get(position).getData();
                    String final_data=data;
                    if(final_data.length()>=150){
                        final_data=final_data.substring(0,150)+" ...";
                    }
                    viewHolder.diaryPageText_data.setText(final_data);
                    db = new DatabaseHandler(getContext());

                    final String finalDay_now = day_now;
                    final String finalDay_now_ordinal = day_now_ordinal;
                    final String finalMonth_now = month_now;
                    final String finalYear_now = year_now;
                    final int pos=position;
                    viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(ViewDiary.this,EditPage_Diary.class);
                            intent.putExtra("id",diaryModelList.get(pos).getId()+"");
                            intent.putExtra("date",diaryModelList.get(pos).getDate());
                            intent.putExtra("title",diaryModelList.get(pos).getTitle());
                            intent.putExtra("body",diaryModelList.get(pos).getData());
                            intent.putExtra("day", finalDay_now);
                            intent.putExtra("day_ordinal", finalDay_now_ordinal);
                            intent.putExtra("month", finalMonth_now);
                            intent.putExtra("year", finalYear_now);
                            intent.putExtra("from", "fromDiaryView");
                            startActivityForResult(intent, 1);
                        }
                    });

                }else{
                    convertView=inflater.inflate(R.layout.layout_new_page_diary,null);
                    viewHolder=new ViewHolder();
                    RelativeLayout layout=(RelativeLayout)convertView.findViewById(R.id.layout);
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(ViewDiary.this,NewPage_Diary.class);
                            startActivityForResult(intent, 2);
                        }
                    });

                    viewHolder.diaryPageText_id=(TextView)convertView.findViewById(R.id.note_id);
                    viewHolder.diaryPageText_day=(TextView)convertView.findViewById(R.id.day);
                    viewHolder.diaryPageText_day_ordinal=(TextView)convertView.findViewById(R.id.ordinal);
                    viewHolder.diaryPageText_month=(TextView)convertView.findViewById(R.id.month);
                    viewHolder.diaryPageText_year=(TextView)convertView.findViewById(R.id.year);
                    viewHolder.diaryPageText_title=(TextView)convertView.findViewById(R.id.title);
                    viewHolder.diaryPageText_data=(TextView)convertView.findViewById(R.id.body);
                    viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
                    viewHolder.circle_dateLayout=(RelativeLayout)convertView.findViewById(R.id.circle_date);
                    viewHolder.diaryPageText_id.setText(""+diaryModelList.get(position).getId());viewHolder.diaryPageText_id.setVisibility(View.GONE);
                    //date operations
                    String day_now = null,day_now_ordinal= null,month_now= null,year_now= null;
                    String date_=null;
                    date_=""+diaryModelList.get(position).getDate();
                    df = new SimpleDateFormat("yyyy-MM-dd");
                    try {

                        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                        Date fetchedDate = df.parse(date_);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(fetchedDate);
                        day_now= ""+cal.get(Calendar.DAY_OF_MONTH);
                        day_now_ordinal=ordinal(cal.get(Calendar.DAY_OF_MONTH));
                        month_now = month_date.format(cal.getTime())+", ";
                        year_now= String.valueOf(cal.get(Calendar.YEAR));
                        viewHolder.diaryPageText_day.setText(day_now);
                        viewHolder.diaryPageText_day_ordinal.setText(day_now_ordinal);
                        viewHolder.diaryPageText_month.setText(month_now);
                        viewHolder.diaryPageText_year.setText(year_now);

                        //set color of the date layout background
                        if(Calendar.DAY_OF_MONTH %2==0){
                            viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle1);
                        }else{
                            viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    viewHolder.diaryPageText_title.setText(diaryModelList.get(position).getTitle());
                    String data=diaryModelList.get(position).getData();
                    String final_data=data;
                    if(final_data.length()>=150){
                        final_data=final_data.substring(0,150)+" ...";
                    }
                    viewHolder.diaryPageText_data.setText(final_data);
                    db = new DatabaseHandler(getContext());

                    final String finalDay_now = day_now;
                    final String finalDay_now_ordinal = day_now_ordinal;
                    final String finalMonth_now = month_now;
                    final String finalYear_now = year_now;
                    final int pos=position;
                    viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(ViewDiary.this,EditPage_Diary.class);
                            intent.putExtra("id",diaryModelList.get(pos).getId()+"");
                            intent.putExtra("date",diaryModelList.get(pos).getDate());
                            intent.putExtra("title",diaryModelList.get(pos).getTitle());
                            intent.putExtra("body",diaryModelList.get(pos).getData());
                            intent.putExtra("day", finalDay_now);
                            intent.putExtra("day_ordinal", finalDay_now_ordinal);
                            intent.putExtra("month", finalMonth_now);
                            intent.putExtra("year", finalYear_now);
                            intent.putExtra("from", "fromDiaryView");
                            startActivityForResult(intent, 1);
                        }
                    });

                }

                convertView.setTag(viewHolder);

            }else if(convertView==null && position!=0){

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String todayDate=df.format(date);

                convertView=inflater.inflate(R.layout.row_daily_diary,null);
                viewHolder=new ViewHolder();
                viewHolder.diaryPageText_id=(TextView)convertView.findViewById(R.id.note_id);
                viewHolder.diaryPageText_day=(TextView)convertView.findViewById(R.id.day);
                viewHolder.diaryPageText_day_ordinal=(TextView)convertView.findViewById(R.id.ordinal);
                viewHolder.diaryPageText_month=(TextView)convertView.findViewById(R.id.month);
                viewHolder.diaryPageText_year=(TextView)convertView.findViewById(R.id.year);
                viewHolder.diaryPageText_title=(TextView)convertView.findViewById(R.id.title);
                viewHolder.diaryPageText_data=(TextView)convertView.findViewById(R.id.body);
                viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
                viewHolder.circle_dateLayout=(RelativeLayout)convertView.findViewById(R.id.circle_date);

                viewHolder.diaryPageText_id.setText(""+diaryModelList.get(position).getId());viewHolder.diaryPageText_id.setVisibility(View.GONE);
                //date operations
                String day_now = null,day_now_ordinal= null,month_now= null,year_now= null;
                String date_=null;
                date_=""+diaryModelList.get(position).getDate();
                df = new SimpleDateFormat("yyyy-MM-dd");
                try {

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    Date fetchedDate = df.parse(date_);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fetchedDate);
                    day_now= ""+cal.get(Calendar.DAY_OF_MONTH);
                    day_now_ordinal=ordinal(cal.get(Calendar.DAY_OF_MONTH));
                    month_now = month_date.format(cal.getTime())+", ";
                    year_now= String.valueOf(cal.get(Calendar.YEAR));
                    viewHolder.diaryPageText_day.setText(day_now);
                    viewHolder.diaryPageText_day_ordinal.setText(day_now_ordinal);
                    viewHolder.diaryPageText_month.setText(month_now);
                    viewHolder.diaryPageText_year.setText(year_now);

                    //set color of the date layout background
                    if(position==0){
                        viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle2);
                    }
                    else if(cal.get(Calendar.DAY_OF_MONTH)%2==0){
                        viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle1);
                    }else{
                        viewHolder.circle_dateLayout.setBackgroundResource(R.drawable.circle);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                viewHolder.diaryPageText_title.setText(diaryModelList.get(position).getTitle());
                String data=diaryModelList.get(position).getData();
                String final_data=data;
                if(final_data.length()>=150){
                    final_data=final_data.substring(0,150)+" ...";
                }
                viewHolder.diaryPageText_data.setText(final_data);
                db = new DatabaseHandler(getContext());

                final String finalDay_now = day_now;
                final String finalDay_now_ordinal = day_now_ordinal;
                final String finalMonth_now = month_now;
                final String finalYear_now = year_now;
                final int pos=position;
                viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ViewDiary.this,EditPage_Diary.class);
                        intent.putExtra("id",diaryModelList.get(pos).getId()+"");
                        intent.putExtra("date",diaryModelList.get(pos).getDate());
                        intent.putExtra("title",diaryModelList.get(pos).getTitle());
                        intent.putExtra("body",diaryModelList.get(pos).getData());
                        intent.putExtra("day", finalDay_now);
                        intent.putExtra("day_ordinal", finalDay_now_ordinal);
                        intent.putExtra("month", finalMonth_now);
                        intent.putExtra("year", finalYear_now);
                        intent.putExtra("from", "fromDiaryView");
                        startActivityForResult(intent, 1);
                    }
                });
                convertView.setTag(viewHolder);
            }
            else{
//                Log.e("val","not null");
                viewHolder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }
    }

    //Page Date adapter for searching pages in the diary
    public class PageDateAdapter extends ArrayAdapter{

        private LayoutInflater inflater;
        public ArrayList<PageDateModel>pageDateModelArrayList;
        private int resource;

        public PageDateAdapter(Context context, int resource,ArrayList<PageDateModel> pageDateModelsList) {
            super(context, resource, pageDateModelsList);
            this.pageDateModelArrayList = pageDateModelsList;
            this.resource = resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return pageDateModelArrayList.size();
        }
        @Override
        public Object getItem(int position) {
            return pageDateModelArrayList.get(position);
        }
        class ViewHolder{
            TextView pageDateText_day;
            TextView pageDateText_day_ordinal;
            TextView pageDateText_month;
            TextView pageDateText_year;
            TextView pageDateText_weekOfMonth;
            RelativeLayout circleLayout;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView=inflater.inflate(R.layout.row_grid_date_of_page,null);
                viewHolder=new ViewHolder();
                viewHolder.pageDateText_day=(TextView)convertView.findViewById(R.id.day);
                viewHolder.pageDateText_day_ordinal=(TextView)convertView.findViewById(R.id.ordinal);
                viewHolder.pageDateText_month=(TextView)convertView.findViewById(R.id.month);
                viewHolder.pageDateText_year=(TextView)convertView.findViewById(R.id.year);
                viewHolder.pageDateText_weekOfMonth=(TextView)convertView.findViewById(R.id.weekOfMonth);
                viewHolder.circleLayout=(RelativeLayout)convertView.findViewById(R.id.circle);
                convertView.setTag(viewHolder);
            } else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            Log.e("pages day", pageDateModelArrayList.get(position).getDay()+" "+position);
            viewHolder.pageDateText_day.setText(pageDateModelArrayList.get(position).getDay());
            viewHolder.pageDateText_day_ordinal.setText(pageDateModelArrayList.get(position).getDay_ordinal());
            viewHolder.pageDateText_month.setText(pageDateModelArrayList.get(position).getMonth());
            viewHolder.pageDateText_year.setText(pageDateModelArrayList.get(position).getYear());
            viewHolder.pageDateText_weekOfMonth.setText(pageDateModelArrayList.get(position).getWeek_of_month());

            viewHolder.circleLayout.setBackgroundResource(images[position % 10]);

            viewHolder.circleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_val = null,title_val = null,body_val= null;
                    DatabaseHandler db = new DatabaseHandler(ViewDiary.this);
//                    String val=db.viewDiaryPage(pageDateModelArrayList.get(position).getId());
                    Cursor cursor=db.viewDiaryPage(pageDateModelArrayList.get(position).getId());
                    if(cursor.moveToFirst()){
                        date_val=cursor.getString(1);
                        title_val=cursor.getString(2);
                        body_val=cursor.getString(3);
                    }

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    Date fetchedDate = null;
                    try {
                        fetchedDate = df.parse(date_val);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fetchedDate);
                    String day_now= ""+cal.get(Calendar.DAY_OF_MONTH);
                    String day_now_ordinal=ordinal(cal.get(Calendar.DAY_OF_MONTH));
                    String month_now = month_date.format(cal.getTime())+", ";
                    String year_now= String.valueOf(cal.get(Calendar.YEAR));

                    Intent intent=new Intent(ViewDiary.this,EditPage_Diary.class);
                    intent.putExtra("id",pageDateModelArrayList.get(position).getId()+"");
                    intent.putExtra("date",date_val);
                    intent.putExtra("title",title_val);
                    intent.putExtra("body",body_val);
                    intent.putExtra("day", day_now);
                    intent.putExtra("day_ordinal", day_now_ordinal);
                    intent.putExtra("month", month_now);
                    intent.putExtra("year", year_now);
                    intent.putExtra("from","searchPageOfDiary");
                    startActivityForResult(intent, 1);
                }
            });

            return convertView;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");
                if(status.equals("ok")){
                    ArrayList<DiaryModel> diaryModelArrayList=db.ViewDiary(); //get diary content form database
                    diaryAdapter=new DiaryAdapter(getApplicationContext(),R.layout.row_daily_diary,diaryModelArrayList);
                    listView.setAdapter(diaryAdapter);

                    dateGridViewLayout = (GridView) dialog.findViewById(R.id.dateGridView);
                    ArrayList<PageDateModel>pageDateModelArrayList=db.ViewDiaryDates(); //array list for searching of pages in diary
                    pageDateAdapter = new PageDateAdapter(getApplicationContext(), R.layout.row_grid_date_of_page, pageDateModelArrayList);
                    dateGridViewLayout.setAdapter(pageDateAdapter);

                }else if(status.equals("deleted")){
                    ArrayList<DiaryModel> diaryModelArrayList=db.ViewDiary(); //get diary content form database
                    diaryAdapter=new DiaryAdapter(getApplicationContext(),R.layout.row_daily_diary,diaryModelArrayList);
                    listView.setAdapter(diaryAdapter);

                    dateGridViewLayout = (GridView) dialog.findViewById(R.id.dateGridView);
                    ArrayList<PageDateModel>pageDateModelArrayList=db.ViewDiaryDates(); //array list for searching of pages in diary
                    pageDateAdapter = new PageDateAdapter(getApplicationContext(), R.layout.row_grid_date_of_page, pageDateModelArrayList);
                    dateGridViewLayout.setAdapter(pageDateAdapter);

                    int count=db.getCountOfDiary();
                    if(count==0){
                        listView.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                        emptyLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ViewDiary.this, NewPage_Diary.class);
                                startActivityForResult(intent, 2);
                            }
                        });
                    }else{
                        emptyLayout.setVisibility(View.GONE);
                    }

                    Toast.makeText(getApplicationContext(),"Page Deleted",Toast.LENGTH_SHORT).show();
                }else if(status.equals("discarded")){
                    Toast.makeText(getApplicationContext(),"Page Discarded",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");
                if(status.equals("added")){
                    db = new DatabaseHandler(getApplicationContext());
                    int count=db.getCountOfDiary();
                    if(count==0){
                        listView.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                        emptyLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(ViewDiary.this,NewPage_Diary.class);
                                startActivityForResult(intent, 2);
                            }
                        });
                    }
                    else{
                        ArrayList<DiaryModel> diaryModelArrayList=db.ViewDiary(); //get diary content form database
                        diaryAdapter=new DiaryAdapter(getApplicationContext(),R.layout.row_daily_diary,diaryModelArrayList);
                        listView.setAdapter(diaryAdapter);
                        listView.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.GONE);
                    }
                }else if(status.equals("discarded")){
                    Toast.makeText(getApplicationContext(),"Page Discarded",Toast.LENGTH_SHORT).show();
                }
            }
        }
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

}
