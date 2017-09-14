package com.bukup.gauravpc.noteit.ToDo;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bukup.gauravpc.noteit.BucketList.BucketListMain;
import com.bukup.gauravpc.noteit.DatabaseHandler;
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
    ToDoAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_main);

        //Database operations
        db = new DatabaseHandler(this);
        int count=db.getCountOfToDoList();
        if(count==0){
            listView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }else{
            ToDoModelArrayList=db.viewTodo();
            blAdapter=new ToDoMain.BLAdapter(getApplicationContext(),R.layout.row_bucket_list,ToDoModelArrayList);
            listView.setAdapter(blAdapter);
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
}
