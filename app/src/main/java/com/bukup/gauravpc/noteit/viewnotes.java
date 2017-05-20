package com.bukup.gauravpc.noteit;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.dailyDiary.ViewDiary;
import com.etsy.android.grid.StaggeredGridView;
import com.github.clans.fab.FloatingActionMenu;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class viewnotes extends AppCompatActivity {

    private ListView notesList;
    ImageView edit,delete;
    DatabaseHandler db;
    TextView empty_text;
    StaggeredGridView gridView;
    ListView listView;
    FloatingActionMenu menu;
    com.github.clans.fab.FloatingActionButton newNoteButton,settingsButton;
    private DrawerLayout mDrawer;
    ImageView hamburgerIcon;
    int showList=0;
    RelativeLayout emptyLayout,mainHeadLayout,searchHeadLayout,MultiNoteLayout;
    NotesAdapter notesAdapter;

    String noteStyle;

    //Navigation Drawer Options
    LinearLayout notesLayout,dailyDiaryLayout,bucketListLayout,slamBookLayout,showListLayout,gridViewLayout,feedbackLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnotes);


        SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        noteStyle=sharedPreferences.getString("showIn", "");
        String newNoteAtTop=sharedPreferences.getString("newNoteAtTop", "");

        //Navigation Drawer Operations
        mDrawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        hamburgerIcon=(ImageView)findViewById(R.id.hamburger_icon);
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {mDrawer.closeDrawer(GravityCompat.START);} else {mDrawer.openDrawer(GravityCompat.START);}}
        });

        notesLayout=(LinearLayout)findViewById(R.id.notes);
        dailyDiaryLayout=(LinearLayout)findViewById(R.id.dailyDiaryLayout);
        bucketListLayout=(LinearLayout)findViewById(R.id.bucketListLayout);
        slamBookLayout=(LinearLayout)findViewById(R.id.slamBookLayout);
        showListLayout=(LinearLayout)findViewById(R.id.showList);
        gridViewLayout=(LinearLayout)findViewById(R.id.showGrid);
        feedbackLayout=(LinearLayout)findViewById(R.id.feedback);
        notesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                } else {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            }
        });
        dailyDiaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(viewnotes.this, ViewDiary.class);
                startActivity(intent);
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

        //header Layouts
        mainHeadLayout=(RelativeLayout)findViewById(R.id.mainHead);
        searchHeadLayout=(RelativeLayout)findViewById(R.id.searchHead);
        searchHeadLayout.setVisibility(View.GONE);
        ImageView searchIconImage=(ImageView)findViewById(R.id.search_icon);

        final SearchView editText=(SearchView)findViewById(R.id.search);
        editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainHeadLayout.setVisibility(View.GONE);
                searchHeadLayout.setVisibility(View.VISIBLE);
                editText.setFocusable(true);
            }
        });
        ImageView goBackImage=(ImageView)findViewById(R.id.goBackImg);
        goBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewnotes.this, viewnotes.class);
                startActivity(intent);
                finish();
            }
        });
        //Header Layout End


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
            ArrayList<notesModel>notesModelList=db.ViewNotes(newNoteAtTop);

            notesAdapter=new NotesAdapter(getApplicationContext(),R.layout.gridview_note,notesModelList);

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
                    noteStyle="gridView";
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
                    noteStyle="listView";
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
                Intent intent = new Intent(viewnotes.this, addnote.class);
                startActivity(intent);
                finish();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewnotes.this, Settings.class);
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


        //item click methods
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String note_id=((TextView)view.findViewById(R.id.note_id)).getText().toString();
//                String title=((TextView)view.findViewById(R.id.title)).getText().toString();
//                String body=((TextView)view.findViewById(R.id.body)).getText().toString();
//                Intent intent=new Intent(viewnotes.this,edit_note.class);
//                intent.putExtra("id",note_id);
//                intent.putExtra("title",title);
//                intent.putExtra("body",body);
//                startActivity(intent);
//                finish();
//            }
//        });
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String note_id=((TextView)view.findViewById(R.id.note_id)).getText().toString();
//                String title=((TextView)view.findViewById(R.id.title)).getText().toString();
//                String body=((TextView)view.findViewById(R.id.body)).getText().toString();
//                Intent intent=new Intent(viewnotes.this,edit_note.class);
//                intent.putExtra("id",note_id);
//                intent.putExtra("title",title);
//                intent.putExtra("body",body);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
    public void openOrCloseNavigationDrawer(){
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            mDrawer.openDrawer(GravityCompat.START);
        }
    }
    public class NotesAdapter extends ArrayAdapter implements Filterable{

        private LayoutInflater inflater;
        public ArrayList<notesModel>notesModelList;
        public ArrayList<notesModel>notesModelListFiltered;
        private int resource;
        CustomFilter customFilter;

        public NotesAdapter(Context context, int resource,ArrayList<notesModel> notesModelList) {
            super(context, resource, notesModelList);
            this.notesModelList = notesModelList;
            this.notesModelListFiltered = notesModelList;
            this.resource = resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return notesModelList.size();
        }

        @Override
        public Object getItem(int position) {
            return notesModelList.get(position);
        }

        class ViewHolder{
            TextView noteText_id;
            TextView noteText_date;
            TextView noteText_title;
            TextView noteText_data;
            CardView noteCard;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if(convertView==null){
                Log.e("pos null",""+position);
                convertView=inflater.inflate(R.layout.gridview_note,null);
                viewHolder=new ViewHolder();
                viewHolder.noteText_id=(TextView)convertView.findViewById(R.id.note_id);
                viewHolder.noteText_date=(TextView)convertView.findViewById(R.id.date);
                viewHolder.noteText_title=(TextView)convertView.findViewById(R.id.title);
                viewHolder.noteText_data=(TextView)convertView.findViewById(R.id.body);
                viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
                convertView.setTag(viewHolder);
            }else{
                Log.e("pos",""+position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            viewHolder.noteText_date=(TextView)convertView.findViewById(R.id.date);
//            viewHolder.noteText_title=(TextView)convertView.findViewById(R.id.title);
//            viewHolder.noteText_data=(TextView)convertView.findViewById(R.id.body);
//            viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
            viewHolder.noteText_id.setText(""+notesModelList.get(position).getId());viewHolder.noteText_id.setVisibility(View.GONE);
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

        @Override
        public Filter getFilter() {
            if(customFilter==null){
                customFilter=new CustomFilter();
            }
            return customFilter;
        }
        class CustomFilter extends Filter{

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults=new FilterResults();

                if(constraint!=null && constraint.length()>0){
                    ArrayList<notesModel> filters=new ArrayList<notesModel>();
                    constraint=constraint.toString().toUpperCase();
                    for(int i=0;i<notesModelListFiltered.size();i++){
                        Log.e("found",constraint+" "+notesModelListFiltered.get(i).getData());
                        if(notesModelListFiltered.get(i).getData().toUpperCase().contains(constraint) || notesModelListFiltered.get(i).getTitle().toUpperCase().contains(constraint)){
                            notesModel notesModel=new notesModel(notesModelListFiltered.get(i).getDate(),notesModelListFiltered.get(i).getTitle(),notesModelListFiltered.get(i).getData());
                            filters.add(notesModel);
                        }
                    }
                    filterResults.count=filters.size();
                    filterResults.values=filters;

                }else{
                    filterResults.count=notesModelListFiltered.size();
                    filterResults.values=notesModelListFiltered;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.count==0){
                    Log.e("found","No");
                    notifyDataSetInvalidated();
                }else{
                    Log.e("found","Yes");
                    notesModelList= (ArrayList<notesModel>) results.values;
                    notifyDataSetChanged();
                    if(noteStyle.equals("gridView")){
                        listView.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                    }
                    else if(noteStyle.equals("listView")){
                        listView.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}