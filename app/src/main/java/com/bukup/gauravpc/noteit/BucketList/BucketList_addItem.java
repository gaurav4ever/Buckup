package com.bukup.gauravpc.noteit.BucketList;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BucketList_addItem extends AppCompatActivity {

    EditText titleEditText,descEditText;
    TextView categoryTextView,targetDateTextView;
    RelativeLayout addItemToBucketListLayout;
    ImageView goBackImageView;
    RelativeLayout bl1Layout,bl2Layout,bl3Layout,bl4Layout,bl5Layout,bl6Layout,bl7Layout,bl8Layout,bl9Layout,bl10Layout,bl11Layout,bl12Layout;
    ArrayList<CategoryItemModel> categoryItemModelArrayList;
    GridView catGridView;
    CatAdapter catAdapter;
    Dialog cd;
    String cat_name_val="-1",cat_detail_val="-1",targetDate="";
    String selectCategoryId="-1";
    String insertedListId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_add_item);

        titleEditText=(EditText)findViewById(R.id.title);
        descEditText=(EditText)findViewById(R.id.desc);
        categoryTextView=(TextView)findViewById(R.id.category);
        targetDateTextView=(TextView)findViewById(R.id.target_date);
        addItemToBucketListLayout=(RelativeLayout)findViewById(R.id.addItemToBucketList);

        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new Dialog(BucketList_addItem.this);
                cd.setContentView(R.layout.layout_category_bucket_list);
                ImageView crossImageView=(ImageView)cd.findViewById(R.id.cross);
                crossImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cd.dismiss();
                    }
                });

                DatabaseHandler db = new DatabaseHandler(getApplication());
                categoryItemModelArrayList=db.getCatBucketList();
                catGridView=(GridView)cd.findViewById(R.id.catGridView);
                catAdapter=new CatAdapter(getApplicationContext(),R.layout.layout_category_bucket_list,categoryItemModelArrayList);
                catGridView.setAdapter(catAdapter);
                cd.show();
            }
        });
        targetDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                new DatePickerDialog(BucketList_addItem.this,myDateListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE)).show();
            }
        });

        addItemToBucketListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=1;
                String title=titleEditText.getText().toString();
                if(title.length()<1){
                    flag=0;
                    titleEditText.setError("Fill Title");
                }
                String cat_id=selectCategoryId;
                String target_date=targetDate;
                if(cat_id.equals("-1")){
                    flag=0;
                    Toast.makeText(getApplicationContext(),"Choose Category",Toast.LENGTH_SHORT).show();
                }else{
                    if(target_date.equals("")){
                        flag=0;
                        Toast.makeText(getApplicationContext(),"Select Start Date",Toast.LENGTH_SHORT).show();
                    }
                }
                String desc=descEditText.getText().toString();
                if(desc.length()<1){
                    flag=0;
                    descEditText.setError("Fill Description");
                }

                if(flag==1) {
                    DatabaseHandler db = new DatabaseHandler(BucketList_addItem.this);
                    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
                    Date date = new Date();
                    String created_at=df.format(date);
                    String updated_at=df.format(date);
                    insertedListId=db.addItem_bucketList(makeFirstUpper(title),desc,target_date,cat_id,created_at,updated_at);
//                    Log.d("id",insertedListId);

                    Intent intent = new Intent();
                    intent.putExtra("status", "added");
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        goBackImageView=(ImageView)findViewById(R.id.goBackImg);
        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    targetDate = arg3 + "/" + arg2 + "/" + arg1;
                    targetDateTextView.setText(targetDate);
//                                showDate(arg1, arg2+1, arg3);
                }
            };
    public class CatAdapter extends ArrayAdapter{
        private LayoutInflater inflater;
        public ArrayList<CategoryItemModel>categoryItemModelArrayList;
        private int resource;

        public CatAdapter(Context context, int resource,ArrayList<CategoryItemModel> categoryItemModelArrayList) {
            super(context, resource, categoryItemModelArrayList);
            this.categoryItemModelArrayList = categoryItemModelArrayList;
            this.resource = resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return categoryItemModelArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return categoryItemModelArrayList.get(position);
        }
        class ViewHolder{
            TextView name;
            TextView detail;
            ImageView img;
            RelativeLayout blLayout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if(convertView==null){
                convertView=inflater.inflate(R.layout.row_grid_category_bucket_list,null);
                viewHolder=new ViewHolder();
                viewHolder.name=(TextView)convertView.findViewById(R.id.cat_name);
                viewHolder.img=(ImageView)convertView.findViewById(R.id.blImage);
                viewHolder.blLayout=(RelativeLayout)convertView.findViewById(R.id.blLayout);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(categoryItemModelArrayList.get(position).getName());
            viewHolder.img.setImageResource(categoryItemModelArrayList.get(position).getImg());
            final int pos=position;



            viewHolder.blLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cat_name_val=categoryItemModelArrayList.get(pos).getName();
                    categoryTextView.setText(cat_name_val);
                    selectCategoryId=categoryItemModelArrayList.get(pos).getId();
                    cd.dismiss();
                }
            });
            viewHolder.blLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    cat_name_val=categoryItemModelArrayList.get(pos).getName();
                    cat_detail_val=categoryItemModelArrayList.get(pos).getDetail();


                    final Dialog dialog = new Dialog(BucketList_addItem.this);
                    dialog.setContentView(R.layout.layout_cat_details);
                    TextView cat_name,cat_detail;
                    cat_name=(TextView)dialog.findViewById(R.id.cat_name);cat_name.setText(cat_name_val);
                    cat_detail=(TextView)dialog.findViewById(R.id.cat_detail);cat_detail.setText(cat_detail_val);
                    dialog.show();

                    ImageView cross=(ImageView)dialog.findViewById(R.id.goBackImg);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    return false;
                }
            });
            return convertView;
        }
    }

    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
