package com.bukup.gauravpc.noteit.dailyDiary;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.DiaryModel;
import com.bukup.gauravpc.noteit.R;
import com.bukup.gauravpc.noteit.Settings;
import com.bukup.gauravpc.noteit.addnote;
import com.bukup.gauravpc.noteit.edit_note;
import com.bukup.gauravpc.noteit.notesModel;
import com.bukup.gauravpc.noteit.viewnotes;
import com.etsy.android.grid.StaggeredGridView;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOError;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewDiary extends AppCompatActivity {

    DatabaseHandler db;
    ListView listView;
    FloatingActionMenu menu;
    com.github.clans.fab.FloatingActionButton newNoteButton,settingsButton;
    private DrawerLayout mDrawer;
    ImageView hamburgerIcon,calendarIcon;
    RelativeLayout emptyLayout;
    DiaryAdapter diaryAdapter;
    ArrayList<PageDateModel>pageDateModelArrayList;
    PageDateModel pageDateModel;
    GridView dateGridViewLayout;
    PageDateAdapter pageDateAdapter;
    
    String pagesDate[]={};
    String pagesId[]={};

    //Navigation Drawer Options
    LinearLayout notesLayout,dailyDiaryLayout,bucketListLayout,slamBookLayout,showListLayout,gridViewLayout,feedbackLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diary);

        //Navigation Drawer Operations
        mDrawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        hamburgerIcon=(ImageView)findViewById(R.id.hamburger_icon);
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                } else {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            }
        });
        //Calendar operations
        calendarIcon=(ImageView)findViewById(R.id.calendar_icon);
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                SimpleDateFormat day_of_week = new SimpleDateFormat("EEEE");
                DateFormat df = new SimpleDateFormat("dd/MM/yy");
                pageDateModelArrayList=new ArrayList<PageDateModel>();
                for(int i=0;i<pagesDate.length;i++) {
                    pageDateModel=new PageDateModel();
                    Date fetchedDate = null;
                    try {
                        fetchedDate = df.parse(pagesDate[i]);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(fetchedDate);
                        pageDateModel.setId(pagesId[i]);
                        pageDateModel.setDay(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                        pageDateModel.setDay_ordinal(ordinal(cal.get(Calendar.DAY_OF_MONTH)));
                        pageDateModel.setMonth(month_date.format(cal.getTime()) + ", ");
                        pageDateModel.setYear(String.valueOf(cal.get(Calendar.YEAR)));
                        pageDateModel.setWeek_of_month(day_of_week.format(cal.getTime()) + "");
                        pageDateModelArrayList.add(pageDateModel);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                final Dialog dialog = new Dialog(ViewDiary.this);
                dialog.setContentView(R.layout.layout_calendar);
                dateGridViewLayout=(GridView)dialog.findViewById(R.id.dateGridView);
                pageDateAdapter=new PageDateAdapter(getApplicationContext(),R.layout.row_grid_date_of_page,pageDateModelArrayList);
                dateGridViewLayout.setAdapter(pageDateAdapter);
                dialog.show();
            }
        });

        emptyLayout=(RelativeLayout)findViewById(R.id.emptyLayout);
        notesLayout=(LinearLayout)findViewById(R.id.notes);
        dailyDiaryLayout=(LinearLayout)findViewById(R.id.dailyDiaryLayout);
        bucketListLayout=(LinearLayout)findViewById(R.id.bucketListLayout);
        slamBookLayout=(LinearLayout)findViewById(R.id.slamBookLayout);
        showListLayout=(LinearLayout)findViewById(R.id.showList);showListLayout.setVisibility(View.GONE);
        gridViewLayout=(LinearLayout)findViewById(R.id.showGrid);gridViewLayout.setVisibility(View.GONE);
        feedbackLayout=(LinearLayout)findViewById(R.id.feedback);
        notesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewDiary.this,viewnotes.class);
                startActivity(intent);
                finish();
            }
        });
        dailyDiaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                } else {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            }
        });
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            }
        });
        //Navigation Drawer End

        //Database operations
        DatabaseHandler db = new DatabaseHandler(this);
        int count=db.getCountOfDiary();
        pagesDate=new String[count];
        pagesId=new String[count];
        if(count==0){
            emptyLayout.setVisibility(View.VISIBLE);
            emptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ViewDiary.this,NewPage_Diary.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else{
            listView=(ListView)findViewById(R.id.list);
            ArrayList<DiaryModel> diaryModelArrayList=db.ViewDiary();
            diaryAdapter=new DiaryAdapter(getApplicationContext(),R.layout.row_daily_diary,diaryModelArrayList);
            listView.setAdapter(diaryAdapter);
            emptyLayout.setVisibility(View.GONE);
        }
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
            Log.e("pos",""+position);
            pagesDate[position]=diaryModelList.get(position).getDate();
            pagesId[position]= String.valueOf(diaryModelList.get(position).getId());
            if(convertView==null && position==0){

                DateFormat df = new SimpleDateFormat("dd/MM/yy");
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
                    df = new SimpleDateFormat("dd/MM/yy");
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
                            startActivity(intent);
                            finish();
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
                            startActivity(intent);
                            finish();
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
                    df = new SimpleDateFormat("dd/MM/yy");
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
                            startActivity(intent);
                            finish();
                        }
                    });

                }

                convertView.setTag(viewHolder);

            }else if(convertView==null && position!=0){

                DateFormat df = new SimpleDateFormat("dd/MM/yy");
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
                df = new SimpleDateFormat("dd/MM/yy");
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
                        startActivity(intent);
                        finish();
                    }
                });
                convertView.setTag(viewHolder);
            }
            else{
                Log.e("val","not null");
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
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView=inflater.inflate(R.layout.row_grid_date_of_page,null);
                viewHolder=new ViewHolder();
                viewHolder.pageDateText_day=(TextView)convertView.findViewById(R.id.day);
                viewHolder.pageDateText_day_ordinal=(TextView)convertView.findViewById(R.id.ordinal);
                viewHolder.pageDateText_month=(TextView)convertView.findViewById(R.id.month);
                viewHolder.pageDateText_year=(TextView)convertView.findViewById(R.id.year);
                viewHolder.pageDateText_weekOfMonth=(TextView)convertView.findViewById(R.id.weekOfMonth);
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

            return convertView;
        }
    }



    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public static String ordinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                return sufixes[i % 10];

        }
    }
}
