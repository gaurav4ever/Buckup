package com.bukup.gauravpc.noteit.BucketList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.Models.BLModel;
import com.bukup.gauravpc.noteit.R;

import java.util.ArrayList;

public class BucketListMain extends AppCompatActivity {

    DatabaseHandler db;
    ListView listView;
    RelativeLayout emptyLayout;
    FloatingActionButton fab;
    ImageView goBackImageView;
    ArrayList<BLModel> blModelArrayList;
    BLAdapter blAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_main);

        fab = (FloatingActionButton) findViewById(R.id.addItemButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BucketListMain.this, BucketList_addItem.class);
                startActivityForResult(intent, 1);
            }
        });
        goBackImageView=(ImageView)findViewById(R.id.goBackImg);
        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //list operations
        emptyLayout=(RelativeLayout)findViewById(R.id.emptyLayout);
        listView=(ListView)findViewById(R.id.list);

        db = new DatabaseHandler(this);
        int count=db.getCountOfBucketList();
        if(count==0){
            listView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }else{
            blModelArrayList=db.viewBL();
            blAdapter=new BLAdapter(getApplicationContext(),R.layout.row_bucket_list,blModelArrayList);
            listView.setAdapter(blAdapter);
            listView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }
    
    public class BLAdapter extends ArrayAdapter{
        private LayoutInflater inflater;
        public ArrayList<BLModel> blModelArrayList;
        private int resource;

        public BLAdapter(Context context, int resource,ArrayList<BLModel> blModelArrayList) {
            super(context, resource, blModelArrayList);
            this.blModelArrayList = blModelArrayList;
            this.resource = resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return blModelArrayList.size();
        }
        @Override
        public Object getItem(int position) {
            return blModelArrayList.get(position);
        }
        @Override
        public int getViewTypeCount() {
            if(blModelArrayList.size()==0){
                return 1;
            }
            return blModelArrayList.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder{
            TextView BL_Text_title;
            TextView BL_Text_desc;
            TextView BL_Text_target_date;
            TextView BL_Text_cat_name;
            ImageView BL_cat_img;
            CardView BLCard;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView=inflater.inflate(R.layout.row_bucket_list,null);
                viewHolder=new ViewHolder();
                viewHolder.BL_Text_title=(TextView)convertView.findViewById(R.id.title);
                viewHolder.BL_Text_desc=(TextView)convertView.findViewById(R.id.desc);
                viewHolder.BL_Text_target_date=(TextView)convertView.findViewById(R.id.target_date);
                viewHolder.BL_Text_cat_name=(TextView)convertView.findViewById(R.id.cat_name);
                viewHolder.BL_cat_img=(ImageView)convertView.findViewById(R.id.bl_cat_img);
                viewHolder.BLCard=(CardView)convertView.findViewById(R.id.BLCard);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }

            //fill the view with the values from the BLModel array object
            viewHolder.BL_Text_title.setText(blModelArrayList.get(position).getTitle());
            viewHolder.BL_Text_desc.setText(blModelArrayList.get(position).getDesc());
            viewHolder.BL_Text_target_date.setText("Target Date: "+blModelArrayList.get(position).getTarget_date());
            viewHolder.BL_Text_cat_name.setText(blModelArrayList.get(position).getCat_name());
            viewHolder.BL_cat_img.setImageResource(blModelArrayList.get(position).getCat_img());

            viewHolder.BLCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(BucketListMain.this,BucketList_editItem.class);
                    intent.putExtra("id", blModelArrayList.get(position).getId());
                    intent.putExtra("title", blModelArrayList.get(position).getTitle());
                    intent.putExtra("desc", blModelArrayList.get(position).getDesc());
                    intent.putExtra("target_date", blModelArrayList.get(position).getTarget_date());
                    intent.putExtra("cat_id", blModelArrayList.get(position).getCat_id());
                    intent.putExtra("cat_name", blModelArrayList.get(position).getCat_name());
                    intent.putExtra("cat_img", ""+blModelArrayList.get(position).getCat_img());
                    intent.putExtra("created_on", ""+blModelArrayList.get(position).getCreated_on());
                    intent.putExtra("updated_on", ""+blModelArrayList.get(position).getUpdated_on());
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
            if (resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");
                if(status.equals("added") || status.equals("ok")){
                    db = new DatabaseHandler(this);
                    int count=db.getCountOfBucketList();
                    if(count==0){
                        listView.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                    }else{
                        blModelArrayList=db.viewBL();
                        blAdapter=new BLAdapter(getApplicationContext(),R.layout.row_bucket_list,blModelArrayList);
                        listView.setAdapter(blAdapter);
                        listView.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.GONE);
                    }
                }else if(status.equals("deleted")){
                    ArrayList<BLModel> blModelArrayList=db.viewBL();
                    blAdapter=new BLAdapter(getApplicationContext(),R.layout.row_bucket_list,blModelArrayList);
                    listView.setAdapter(blAdapter);

                    int count=db.getCountOfBucketList();
                    if(count==0){
                        listView.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                    }else{
                        listView.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
