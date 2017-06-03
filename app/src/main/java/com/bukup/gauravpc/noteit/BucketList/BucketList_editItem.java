package com.bukup.gauravpc.noteit.BucketList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BucketList_editItem extends AppCompatActivity {

    private android.os.Handler handler;
    String user_id,id_val,title_val,desc_val,target_date_val,cat_id_val,cat_name_val1,cat_name_img;
    EditText titleEditText,descEditText;
    TextView categoryTextView,targetDateTextView,createdOnTextView,updatedOnTextView;
    RelativeLayout updateItemToBucketListButton;
    ImageView goBackImageView,deleteItemImageView;
    ArrayList<CategoryItemModel> categoryItemModelArrayList;
    GridView catGridView;
    CatAdapter catAdapter;
    Dialog cd;
    String cat_name_val="-1",cat_detail_val="-1",targetDate="";
    String selectCategoryId="-1";
    String created_on_val,updated_on_val;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_edit_item);

        sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        user_id=sharedPreferences.getString("user_id", " ");

        Intent intent=getIntent();

        id_val=intent.getStringExtra("id");
        title_val=intent.getStringExtra("title");
        desc_val=intent.getStringExtra("desc");
        targetDate=target_date_val=intent.getStringExtra("target_date");
        selectCategoryId=cat_id_val=intent.getStringExtra("cat_id");
        cat_name_val1=intent.getStringExtra("cat_name");
        cat_name_img=intent.getStringExtra("cat_img");
        created_on_val=intent.getStringExtra("created_on");
        updated_on_val=intent.getStringExtra("updated_on");

        titleEditText=(EditText)findViewById(R.id.title);titleEditText.setText(title_val);
        descEditText=(EditText)findViewById(R.id.desc);descEditText.setText(desc_val);
        categoryTextView=(TextView)findViewById(R.id.category);categoryTextView.setText(cat_name_val1);
        targetDateTextView=(TextView)findViewById(R.id.target_date);targetDateTextView.setText(target_date_val);

        updateItemToBucketListButton=(RelativeLayout)findViewById(R.id.updateItemToBucketList);

        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new Dialog(BucketList_editItem.this);
                cd.setContentView(R.layout.layout_category_bucket_list);
                ImageView crossImageView = (ImageView) cd.findViewById(R.id.cross);
                crossImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cd.dismiss();
                    }
                });

                DatabaseHandler db = new DatabaseHandler(getApplication());
                categoryItemModelArrayList = db.getCatBucketList();
                catGridView = (GridView) cd.findViewById(R.id.catGridView);
                catAdapter = new CatAdapter(getApplicationContext(), R.layout.layout_category_bucket_list, categoryItemModelArrayList);
                catGridView.setAdapter(catAdapter);
                cd.show();
            }
        });
        targetDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(BucketList_editItem.this);
                dialog.setContentView(R.layout.layout_date_bucket_list);
                TextView close=(TextView)dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                CalendarView calendarView=(CalendarView)dialog.findViewById(R.id.calendarView);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                        Toast.makeText(getApplicationContext(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_SHORT).show();
                        targetDate = dayOfMonth + "/" + month + "/" + year;
                    }
                });
                TextView selectDate=(TextView)dialog.findViewById(R.id.select);
                selectDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        targetDateTextView.setText(targetDate);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        updateItemToBucketListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=1;
                String title=titleEditText.getText().toString();
                if(title.length()<1){
                    flag=0;
                    titleEditText.setError("Fill Title");
                }
                String cat_id=selectCategoryId;
                String target_date=targetDateTextView.getText().toString();
                if(cat_id.equals("-1")){
                    flag=0;
                    Toast.makeText(getApplicationContext(), "Choose Category", Toast.LENGTH_SHORT).show();
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
                    DatabaseHandler db = new DatabaseHandler(BucketList_editItem.this);
                    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
                    Date date = new Date();
                    String updated_at=df.format(date);
                    db.updateItem_bucketList(id_val, makeFirstUpper(title), desc, target_date, cat_id,created_on_val, updated_at);
//                    Log.d("id",insertedListId);

                    Intent intent = new Intent();
                    intent.putExtra("status", "ok");
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        createdOnTextView=(TextView)findViewById(R.id.created_on);createdOnTextView.setText("Created On:   "+parseDate(created_on_val));
        updatedOnTextView=(TextView)findViewById(R.id.updated_on);updatedOnTextView.setText("Updated On:   "+parseDate(updated_on_val));


        deleteItemImageView=(ImageView)findViewById(R.id.delete);
        deleteItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(BucketList_editItem.this);
                dialog.setContentView(R.layout.layout_ask_before_delete);
                TextView t1_text=(TextView)dialog.findViewById(R.id.t1);
                t1_text.setText("Delete\nItem?");
                CardView yes,no;
                yes=(CardView)dialog.findViewById(R.id.yesLayout);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseHandler db = new DatabaseHandler(BucketList_editItem.this);
                        db.deleteItemFromBucketList(Integer.parseInt(id_val));
                        db.close();

                        runnable_delete(id_val);

                        Toast.makeText(getApplicationContext(),"Item Deleted",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("status", "deleted");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                no=(CardView)dialog.findViewById(R.id.noLayout);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
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
    public void runnable_delete(final String id_val){
        handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                String url="https://buckupapp.herokuapp.com/backup/delete";
                RequestQueue requestQueue=new Volley().newRequestQueue(getApplicationContext());
                StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("status", "item deleted");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("status","failed");
                        Log.d("error",""+error);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<String,String>();
                        params.put("user_id",user_id);
                        params.put("table","bucket_list");
                        params.put("id",id_val);
                        return params;
                    }
                };
                int socketTimeout = 10000;//30 seconds
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            }
        };
        handler.post(runnable);
    }
    public class CatAdapter extends ArrayAdapter {
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


                    final Dialog dialog = new Dialog(BucketList_editItem.this);
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
