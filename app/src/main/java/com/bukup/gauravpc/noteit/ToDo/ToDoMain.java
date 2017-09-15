package com.bukup.gauravpc.noteit.ToDo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

                        RefreshList();
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
            TextView Todo_Text_date;
            ImageView doneImgView;
            CardView TodoCard;
        }
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ToDoMain.ToDoAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView=inflater.inflate(R.layout.row_todo_list,null);
                viewHolder=new ToDoMain.ToDoAdapter.ViewHolder();
                viewHolder.Todo_Text_desc=(TextView)convertView.findViewById(R.id.desc);
                viewHolder.Todo_Text_date=(TextView)convertView.findViewById(R.id.date);
                viewHolder.doneImgView=(ImageView)convertView.findViewById(R.id.doneImg);
                viewHolder.TodoCard=(CardView)convertView.findViewById(R.id.todoCard);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ToDoMain.ToDoAdapter.ViewHolder)convertView.getTag();
            }

            //fill the view with the values from the BLModel array object
            String isDone=TodoModelArrayList.get(position).getIsDone();

            if(isDone.equals("1")){
                Log.d("isDone",isDone);
                viewHolder.Todo_Text_desc.setTextColor(Color.parseColor("#616161"));
                viewHolder.Todo_Text_desc.setPaintFlags(viewHolder.Todo_Text_desc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else viewHolder.doneImgView.setVisibility(View.GONE);
            viewHolder.Todo_Text_desc.setText(TodoModelArrayList.get(position).getDesc());
            viewHolder.Todo_Text_date.setText(parseDate(TodoModelArrayList.get(position).getUpdated_on()));

            viewHolder.TodoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(ToDoMain.this);
                    View parentView=getLayoutInflater().inflate(R.layout.layout_todo_item_view, null);

                    final EditText itemDesc;
                    TextView saveTextView,cancelTextView,createdOnDate,updatedOnDate;
                    final CheckBox checkBox;
                    itemDesc=(EditText)parentView.findViewById(R.id.itemText);
                    itemDesc.setText(TodoModelArrayList.get(position).getDesc());
                    createdOnDate=(TextView)parentView.findViewById(R.id.created_on);createdOnDate.setText("created on: "+parseDate(TodoModelArrayList.get(position).getCreated_on()));
                    updatedOnDate=(TextView)parentView.findViewById(R.id.updated_on);updatedOnDate.setText("created on: "+parseDate(TodoModelArrayList.get(position).getUpdated_on()));
//                    Mark as Done Checkbox
                    checkBox=(CheckBox)parentView.findViewById(R.id.isItemDone);
                    if(TodoModelArrayList.get(position).getIsDone().equals("1"))checkBox.setChecked(true);
                    else checkBox.setChecked(false);

                    saveTextView=(TextView)parentView.findViewById(R.id.save);
                    saveTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String id=TodoModelArrayList.get(position).getId();
                            String desc=itemDesc.getText().toString();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            String newDate=df.format(date);
                            String isDone="0";
                            if(checkBox.isChecked())isDone="1";

                            db.updateToDoList(id,desc,newDate,isDone);
                            RefreshList();
                            bottomSheetDialog.dismiss();
                        }
                    });
                    cancelTextView=(TextView)parentView.findViewById(R.id.cancel);
                    cancelTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });


                    bottomSheetDialog.setContentView(parentView);
                    bottomSheetDialog.show();
                }
            });

            return convertView;
        }
    }

    public void RefreshList(){
        ToDoModelArrayList=db.viewTodo();
        toDoAdapter=new ToDoMain.ToDoAdapter(getApplicationContext(),R.layout.row_todo_list,ToDoModelArrayList);
        listView.setAdapter(toDoAdapter);
    }

    public String parseDate(String date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
