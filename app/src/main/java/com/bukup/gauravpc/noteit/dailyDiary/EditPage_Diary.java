package com.bukup.gauravpc.noteit.dailyDiary;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditPage_Diary extends AppCompatActivity {

    private android.os.Handler handler;
    EditText new_note;
    EditText title,body;
    ImageView saveImg;
    TextView dairyPageText_day,dairyPageText_day_ordinal,dairyPageText_month,dairyPageText_year;
    String user_id,id_val,date_val,title_val,body_val,day_val,day_ordinal_val,month_val,year_val;
    String ifComeFromSearchPageOfDiary="";
    String title1="",body1="";
    String titleBeforeChange="",titleAfterChange="",bodyBeforeAnyChange="",bodyAfterAnyChange="";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page__diary);

        sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        user_id=sharedPreferences.getString("user_id", " ");

        Intent intent=getIntent();

        id_val=intent.getStringExtra("id");
        date_val=intent.getStringExtra("date");
        titleBeforeChange=title_val=intent.getStringExtra("title");
        bodyBeforeAnyChange=body_val=intent.getStringExtra("body");
        day_val=intent.getStringExtra("day");
        day_ordinal_val=intent.getStringExtra("day_ordinal");
        month_val=intent.getStringExtra("month");
        year_val=intent.getStringExtra("year");
        ifComeFromSearchPageOfDiary=intent.getStringExtra("from");
//        Log.d("ifComeFromSearchPageOfDiary",ifComeFromSearchPageOfDiary);

        title=(EditText)findViewById(R.id.title);title.setText(title_val);
        body=(EditText)findViewById(R.id.body);body.setText(body_val);

        dairyPageText_day=(TextView)findViewById(R.id.day);dairyPageText_day.setText(day_val);
        dairyPageText_day_ordinal=(TextView) findViewById(R.id.ordinal);dairyPageText_day_ordinal.setText(day_ordinal_val);
        dairyPageText_month=(TextView) findViewById(R.id.month);dairyPageText_month.setText(month_val);
        dairyPageText_year=(TextView) findViewById(R.id.year);dairyPageText_year.setText(year_val);

        RelativeLayout saveLayout,audioLayout,imageLayout,backupLayout,infoLayout,deleteNoteLayout;
        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePage();
            }
        });

        audioLayout=(RelativeLayout)findViewById(R.id.audio);
        audioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

                try {
                    startActivityForResult(i, 100);
                }catch(ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(), "Speech To Text not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteNoteLayout=(RelativeLayout)findViewById(R.id.delete);
        deleteNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(EditPage_Diary.this);
                dialog.setContentView(R.layout.layout_ask_before_delete);
                TextView t1_text=(TextView)dialog.findViewById(R.id.t1);
                t1_text.setText("Delete\nPage?");
                CardView yes,no;
                yes=(CardView)dialog.findViewById(R.id.yesLayout);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseHandler db = new DatabaseHandler(EditPage_Diary.this);
                        db.deletePage_diary(Integer.parseInt(id_val));
                        db.close();

                        runnable_delete(id_val);

                        Toast.makeText(getApplicationContext(),"Page Deleted",Toast.LENGTH_SHORT).show();
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
                        params.put("table","diary");
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

    public void updatePage(){
        title1=title.getText().toString();
        body1=body.getText().toString();
        if (title1.length() < 1) {
            title.setText("No Title");
        }

        final DatabaseHandler db = new DatabaseHandler(EditPage_Diary.this);
        db.updatePage_diary(id_val, date_val, makeFirstUpper(title.getText().toString()), body.getText().toString());
        db.close();

        Intent intent = new Intent();
        intent.putExtra("status", "ok");
        setResult(RESULT_OK, intent);
        finish();
    }
    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    public void onBackPressed() {
        if(title1.length()<1 && body1.length()<1){
            titleAfterChange=title.getText().toString();
            bodyAfterAnyChange=body.getText().toString();

            if(titleAfterChange.equals(titleBeforeChange) && bodyAfterAnyChange.equals(bodyBeforeAnyChange)){
                super.onBackPressed();
            }else{
                final Dialog d = new Dialog(EditPage_Diary.this);
                d.setContentView(R.layout.layout_discard_changes);
                TextView discardTextView=(TextView)d.findViewById(R.id.discard);
                TextView saveTextView=(TextView)d.findViewById(R.id.save);
                discardTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("status", "discarded");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                saveTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePage();
                    }
                });
                d.show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){

            case 100: if(resultCode==RESULT_OK && data!=null){
                body=(EditText)findViewById(R.id.body);
                String text=body.getText().toString();
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(text.length()<1){
                    text+=""+result.get(0);
                }
                else
                    text+="\n"+result.get(0);
                body.setText(text);
            }
                break;
        }
    }

}
