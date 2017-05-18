package com.bukup.gauravpc.noteit.dailyDiary;

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

    int flag=0;

    DatabaseHandler db;
    ListView listView;
    FloatingActionMenu menu;
    com.github.clans.fab.FloatingActionButton newNoteButton,settingsButton;
    private DrawerLayout mDrawer;
    ImageView hamburgerIcon;
    RelativeLayout emptyLayout;
    DiaryAdapter diaryAdapter;

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
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;

            if(convertView==null){
                if(position==0){
                    DateFormat df = new SimpleDateFormat("dd/MM/yy");
                    Date date = new Date();
                    String todayDate=df.format(date);

                    if(diaryModelList.get(position).getDate().equals(todayDate)){
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
                        Log.e("yo","1");
                    }else{
                        convertView=inflater.inflate(R.layout.layout_new_page_diary,null);
                        viewHolder=new ViewHolder();
                        flag=1;
                        Log.e("yo","2");
                    }
                }else{
                    Log.e("yo","3");
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

                }
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(flag==0){
                viewHolder.diaryPageText_id.setText(""+diaryModelList.get(position).getId());viewHolder.diaryPageText_id.setVisibility(View.GONE);
                //date operations
                String day_now = null,day_now_ordinal= null,month_now= null,year_now= null;

                String date=""+diaryModelList.get(position).getDate();
                DateFormat df = new SimpleDateFormat("dd/MM/yy");
                try {

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    Date fetchedDate = df.parse(date);
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
                }else{

                }
                viewHolder.diaryPageText_data.setText(final_data);
                db = new DatabaseHandler(getContext());

                final String finalDay_now = day_now;
                final String finalDay_now_ordinal = day_now_ordinal;
                final String finalMonth_now = month_now;
                final String finalYear_now = year_now;

                viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ViewDiary.this,EditPage_Diary.class);
                        intent.putExtra("id",diaryModelList.get(position).getId()+"");
                        intent.putExtra("date",diaryModelList.get(position).getDate());
                        intent.putExtra("title",diaryModelList.get(position).getTitle());
                        intent.putExtra("body",diaryModelList.get(position).getData());
                        intent.putExtra("day", finalDay_now);
                        intent.putExtra("day_ordinal", finalDay_now_ordinal);
                        intent.putExtra("month", finalMonth_now);
                        intent.putExtra("year", finalYear_now);
                        startActivity(intent);
                        finish();
                    }
                });

            }else{
                RelativeLayout layout=(RelativeLayout)convertView.findViewById(R.id.layout);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ViewDiary.this,NewPage_Diary.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
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
