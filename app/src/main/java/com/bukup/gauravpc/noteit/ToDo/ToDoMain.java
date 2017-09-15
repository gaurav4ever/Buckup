package com.bukup.gauravpc.noteit.ToDo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bukup.gauravpc.noteit.BucketList.BucketListMain;
import com.bukup.gauravpc.noteit.BucketList.BucketList_editItem;
import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.Models.BLModel;
import com.bukup.gauravpc.noteit.Models.ToDoModel;
import com.bukup.gauravpc.noteit.Notes.edit_note;
import com.bukup.gauravpc.noteit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ToDoMain extends AppCompatActivity {

    ImageView goBackImage,addItemImage;
    DatabaseHandler db;
    ListView listView;
    RelativeLayout emptyLayout;
    ArrayList<ToDoModel> ToDoModelArrayList;
    ToDoAdapter toDoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_main);
        emptyLayout=(RelativeLayout)findViewById(R.id.emptyLayout);
        listView=(ListView)findViewById(R.id.todoItemList);
        //Database operations
        db = new DatabaseHandler(this);
        int count=db.getCountOfToDoList();
        if(count==0){
            listView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }else{
            ToDoModelArrayList=db.viewTodo();
            toDoAdapter=new ToDoMain.ToDoAdapter(getApplicationContext(),R.layout.row_todo_list,ToDoModelArrayList);
            listView.setAdapter(toDoAdapter);
            listView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }

        goBackImage=(ImageView)findViewById(R.id.goBackImg);
        goBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addItemImage=(ImageView)findViewById(R.id.addItem);
        addItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(ToDoMain.this);
                View parentView=getLayoutInflater().inflate(R.layout.layout_additem_todo_bottom_sheet, null);
                final EditText itemTextView=(EditText)parentView.findViewById(R.id.itemText);
                CardView addItemCardView=(CardView)parentView.findViewById(R.id.addItemCard);
                addItemCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text=itemTextView.getText().toString();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        String itemDate=df.format(date);
                        db.addItem_TODOList(text,itemDate);

                        ToDoModelArrayList=db.viewTodo();
                        toDoAdapter=new ToDoMain.ToDoAdapter(getApplicationContext(),R.layout.row_todo_list,ToDoModelArrayList);
                        listView.setAdapter(toDoAdapter);
                        bottomSheetDialog.dismiss();
                    }
                });


                bottomSheetDialog.setContentView(parentView);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public class ToDoAdapter extends ArrayAdapter {
        private LayoutInflater inflater;
        public ArrayList<ToDoModel> TodoModelArrayList;
        private int resource;

        public ToDoAdapter(Context context, int resource, ArrayList<ToDoModel> TodoModelArrayList) {
            super(context, resource, TodoModelArrayList);
            this.TodoModelArrayList = TodoModelArrayList;
            this.resource = resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return TodoModelArrayList.size();
        }
        @Override
        public Object getItem(int position) {
            return TodoModelArrayList.get(position);
        }
        @Override
        public int getViewTypeCount() {
            if(TodoModelArrayList.size()==0){
                return 1;
            }
            return TodoModelArrayList.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder{
            TextView Todo_Text_desc;
            TextView Todo_Text_created_on_date;
            TextView Todo_Text_updated_on_date;
            TextView Todo_Text_isDone;
            CardView TodoCard;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ToDoMain.ToDoAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView=inflater.inflate(R.layout.row_todo_list,null);
                viewHolder=new ToDoMain.ToDoAdapter.ViewHolder();
                viewHolder.Todo_Text_desc=(TextView)convertView.findViewById(R.id.desc);
                viewHolder.TodoCard=(CardView)convertView.findViewById(R.id.todoCard);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ToDoMain.ToDoAdapter.ViewHolder)convertView.getTag();
            }

            //fill the view with the values from the BLModel array object
            viewHolder.Todo_Text_desc.setText(TodoModelArrayList.get(position).getDesc());

            viewHolder.TodoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(ToDoMain.this);
                    dialog.setContentView(R.layout.layout_todo_item_view);
                    dialog.show();
                }
            });

            return convertView;
        }
    }
}
