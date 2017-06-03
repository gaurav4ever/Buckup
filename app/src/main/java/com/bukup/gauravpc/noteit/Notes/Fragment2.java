package com.bukup.gauravpc.noteit.Notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.Models.notesModel;
import com.bukup.gauravpc.noteit.Notes.addnote;
import com.bukup.gauravpc.noteit.Notes.edit_note;
import com.bukup.gauravpc.noteit.R;
import com.etsy.android.grid.StaggeredGridView;
import com.github.clans.fab.FloatingActionMenu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.transform.Result;

/**
 * Created by Gaurav Sharma on 28-05-2017.
 */
public class Fragment2 extends Fragment{

    private ListView notesList;
    DatabaseHandler db;
    StaggeredGridView gridView;
    ListView listView;
    FloatingActionMenu menu;
    com.github.clans.fab.FloatingActionButton newNoteButton,settingsButton,searchButton;
    private DrawerLayout mDrawer;
    ImageView hamburgerIcon,goBackImage;
    int showList=0;
    RelativeLayout emptyLayout,mainHeadLayout,searchHeadLayout,MultiNoteLayout;
    NotesAdapter notesAdapter;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment2,null);


        searchHeadLayout=(RelativeLayout)v.findViewById(R.id.searchHead);
        searchHeadLayout.setVisibility(View.GONE);
        final SearchView editText=(SearchView)v.findViewById(R.id.search);
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

        //Database operations
        db = new DatabaseHandler(getActivity());

        int count=db.getCount();
        emptyLayout=(RelativeLayout)v.findViewById(R.id.emptyLayout);
        gridView=(StaggeredGridView)v.findViewById(R.id.grid);
        if(count==0){
            emptyLayout.setVisibility(View.VISIBLE);
        }
        else{
            ArrayList<notesModel> notesModelList=db.ViewNotes();
            notesAdapter=new NotesAdapter(getActivity(),R.layout.gridview_note,notesModelList);
            gridView.setAdapter(notesAdapter);
            emptyLayout.setVisibility(View.GONE);
        }

        //Floating menu and button operations
        menu=(FloatingActionMenu)v.findViewById(R.id.options);
        menu.setClosedOnTouchOutside(true);
        menu.close(true);

        newNoteButton=(com.github.clans.fab.FloatingActionButton)v.findViewById(R.id.addNotes);
        searchButton=(com.github.clans.fab.FloatingActionButton)v.findViewById(R.id.searchNotes);

        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);
                Intent intent = new Intent(getActivity(), addnote.class);
                startActivityForResult(intent, 2);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);
                menu.setVisibility(View.GONE);
                searchHeadLayout.setVisibility(View.VISIBLE);
                editText.setFocusable(true);
            }
        });

        searchView=(SearchView)v.findViewById(R.id.search);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchHeadLayout.setVisibility(View.GONE);
                return false;
            }
        });
        goBackImage=(ImageView)v.findViewById(R.id.goBackImg);
        goBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(getActivity());
                ArrayList<notesModel> notesModelList=db.ViewNotes();
                notesAdapter=new NotesAdapter(getActivity(),R.layout.gridview_note,notesModelList);
                gridView.setAdapter(notesAdapter);
                searchHeadLayout.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
            }
        });

        return v;
    }
    public class NotesAdapter extends ArrayAdapter implements Filterable {

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
            inflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
//                Log.e("pos null", "" + position);
                convertView=inflater.inflate(R.layout.gridview_note,null);
                viewHolder=new ViewHolder();
                viewHolder.noteText_id=(TextView)convertView.findViewById(R.id.note_id);
                viewHolder.noteText_date=(TextView)convertView.findViewById(R.id.date);
                viewHolder.noteText_title=(TextView)convertView.findViewById(R.id.title);
                viewHolder.noteText_data=(TextView)convertView.findViewById(R.id.body);
                viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
                convertView.setTag(viewHolder);
            }else{
//                Log.e("pos",""+position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            viewHolder.noteText_date=(TextView)convertView.findViewById(R.id.date);
//            viewHolder.noteText_title=(TextView)convertView.findViewById(R.id.title);
//            viewHolder.noteText_data=(TextView)convertView.findViewById(R.id.body);
//            viewHolder.noteCard=(CardView)convertView.findViewById(R.id.note);
            viewHolder.noteText_id.setText(""+notesModelList.get(position).getId());viewHolder.noteText_id.setVisibility(View.GONE);
            viewHolder.noteText_date.setText("Edited On:   "+parseDate(notesModelList.get(position).getDate()));
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
                    Intent intent=new Intent(getActivity(),edit_note.class);
                    intent.putExtra("id",notesModelList.get(position).getId()+"");
                    intent.putExtra("title",notesModelList.get(position).getTitle());
                    intent.putExtra("body", notesModelList.get(position).getData());
                    startActivityForResult(intent, 1);
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
//                        Log.e("found",constraint+" "+notesModelListFiltered.get(i).getData());
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
//                    Log.e("found","No");
                    notifyDataSetInvalidated();
                }else{
//                    Log.e("found","Yes");
                    notesModelList= (ArrayList<notesModel>) results.values;
                    notifyDataSetChanged();
                }
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");
                if(status.equals("ok")){
                    ArrayList<notesModel> notesModelList=db.ViewNotes();
                    notesAdapter=new NotesAdapter(getActivity(),R.layout.gridview_note,notesModelList);
                    gridView.setAdapter(notesAdapter);
                }
                else if(status.equals("deleted")){

                    ArrayList<notesModel> notesModelList=db.ViewNotes();
                    notesAdapter=new NotesAdapter(getActivity(),R.layout.gridview_note,notesModelList);
                    gridView.setAdapter(notesAdapter);

                    int count=db.getCount();
                    if(count==0){
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        emptyLayout.setVisibility(View.GONE);
                    }
                    Toast.makeText(getActivity(),"Note Deleted",Toast.LENGTH_SHORT).show();
                }else if(status.equals("discarded")){
                    Toast.makeText(getActivity(),"Note Discarded",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");
                if(status.equals("added")){
                    db = new DatabaseHandler(getActivity());
                    int count=db.getCount();
                    if(count==0){
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        ArrayList<notesModel> notesModelList=db.ViewNotes();
                        notesAdapter=new NotesAdapter(getActivity(),R.layout.gridview_note,notesModelList);
                        gridView.setAdapter(notesAdapter);
                        emptyLayout.setVisibility(View.GONE);
                    }
                }else if(status.equals("discarded")){
                    Toast.makeText(getActivity(),"Note Discarded",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public String parseDate(String date){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        Date fetchedDate = null;
        try {
            fetchedDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fetchedDate);
        String day_now= ""+cal.get(Calendar.DAY_OF_MONTH);
        String day_now_ordinal=ordinal(cal.get(Calendar.DAY_OF_MONTH));
        String month_now = month_date.format(cal.getTime())+", ";
        String year_now= String.valueOf(cal.get(Calendar.YEAR));

        return day_now+""+day_now_ordinal+" "+month_now+" "+year_now;
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
