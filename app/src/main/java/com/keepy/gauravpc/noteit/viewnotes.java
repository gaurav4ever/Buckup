package com.keepy.gauravpc.noteit;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.github.clans.fab.FloatingActionMenu;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.List;


public class viewnotes extends AppCompatActivity {

    private ListView notesList;
    ImageView edit,delete;
    DatabaseHandler db;
    TextView empty_text;
    StaggeredGridView gridView;
    ListView listView;
    FloatingActionMenu menu;
    com.github.clans.fab.FloatingActionButton newNoteButton,settingsButton;
    private FlowingDrawer mDrawer;
    ImageView hamburgerIcon;
    int showList=0;
    RelativeLayout emptyLayout;


    //Navigation Drawer Options
    LinearLayout notesLayout,showListLayout,gridViewLayout,feedbackLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnotes);


        SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        String noteStyle=sharedPreferences.getString("showIn", "");
        String newNoteAtTop=sharedPreferences.getString("newNoteAtTop", "");

        //Navigation Drawer Operations
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
            }
        });
        hamburgerIcon=(ImageView)findViewById(R.id.hamburger_icon);
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isMenuVisible()) {mDrawer.closeMenu();} else {mDrawer.openMenu();}}
        });

        notesLayout=(LinearLayout)findViewById(R.id.notes);
        showListLayout=(LinearLayout)findViewById(R.id.showList);
        gridViewLayout=(LinearLayout)findViewById(R.id.showGrid);
        feedbackLayout=(LinearLayout)findViewById(R.id.feedback);
        notesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isMenuVisible()) {
                    mDrawer.closeMenu();
                } else {
                    mDrawer.openMenu();
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

        //Database operations
        DatabaseHandler db = new DatabaseHandler(this);

        int count=db.getCount();
        emptyLayout=(RelativeLayout)findViewById(R.id.emptyLayout);
        if(count==0){
            emptyLayout.setVisibility(View.VISIBLE);
            gridViewLayout.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {openOrCloseNavigationDrawer();}});
            showListLayout.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {openOrCloseNavigationDrawer();}});
        }
        else{
            listView=(ListView)findViewById(R.id.list);
            gridView=(StaggeredGridView)findViewById(R.id.grid);
            List<notesModel>notesModelList=db.ViewNotes(newNoteAtTop);

            NotesAdapter notesAdapter=new NotesAdapter(getApplicationContext(),R.layout.gridview_note,notesModelList);

            gridView.setAdapter(notesAdapter);
            listView.setAdapter(notesAdapter);
            emptyLayout.setVisibility(View.GONE);

            if(noteStyle.equals("gridView")){
                listView.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            }
            else if(noteStyle.equals("listView")){
                listView.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
            }

            //navigation for gridView and ListView only when there is a note in database
            gridViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                    showListLayout.setVisibility(View.VISIBLE);
                    gridViewLayout.setVisibility(View.GONE);
                    openOrCloseNavigationDrawer();
                    SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("showIn", "gridView");
                    editor.commit();
                }
            });
            showListLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                    showListLayout.setVisibility(View.GONE);
                    gridViewLayout.setVisibility(View.VISIBLE);
                    openOrCloseNavigationDrawer();
                    SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("showIn", "listView");
                    editor.commit();
                }
            });

        }

        //Floating menu and button operations
        menu=(FloatingActionMenu)findViewById(R.id.options);
        menu.setClosedOnTouchOutside(true);
        newNoteButton=(com.github.clans.fab.FloatingActionButton)findViewById(R.id.addNotes);
        settingsButton=(com.github.clans.fab.FloatingActionButton)findViewById(R.id.settings);
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(viewnotes.this,addnote.class);
                startActivity(intent);
                finish();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(viewnotes.this,Settings.class);
                startActivity(intent);
            }
        });

        if(noteStyle.equals("listView")) {
            showListLayout.setVisibility(View.GONE);
            gridViewLayout.setVisibility(View.VISIBLE);
        }else{
            showListLayout.setVisibility(View.VISIBLE);
            gridViewLayout.setVisibility(View.GONE);
        }
    }
    public void openOrCloseNavigationDrawer(){
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            mDrawer.openMenu();
        }
    }
    public class NotesAdapter extends ArrayAdapter{

        private LayoutInflater inflater;
        public List<notesModel>notesModelList;
        private int resource;

        public NotesAdapter(Context context, int resource,List<notesModel> notesModelList) {
            super(context, resource, notesModelList);
            this.notesModelList = notesModelList;
            this.resource = resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            TextView noteText_date;
            TextView noteText_title;
            TextView noteText_data;
            CardView noteCard;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if(convertView==null){
                convertView=inflater.inflate(R.layout.gridview_note,null);
                viewHolder=new ViewHolder();
                viewHolder.noteText_date=(TextView)convertView.findViewById(R.id.date);
                viewHolder.noteText_title=(TextView)convertView.findViewById(R.id.title);
                viewHolder.noteText_data=(TextView)convertView.findViewById(R.id.body);
                viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }

            viewHolder.noteText_date.setText(notesModelList.get(position).getDate());
            viewHolder.noteText_title.setText(notesModelList.get(position).getTitle());
            String data=notesModelList.get(position).getData();
            String final_data=data;
            if(final_data.length()>=140){
                final_data=final_data.substring(0,140)+" ...";
            }
            viewHolder.noteText_data.setText(final_data);

            db = new DatabaseHandler(getContext());

            viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(viewnotes.this,edit_note.class);
                    intent.putExtra("id",notesModelList.get(position).getId()+"");
                    intent.putExtra("title",notesModelList.get(position).getTitle());
                    intent.putExtra("body",notesModelList.get(position).getData());
                    startActivity(intent);
                    finish();
                }
            });

            return convertView;
        }
    }
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}