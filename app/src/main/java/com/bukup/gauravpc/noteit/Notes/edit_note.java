package com.bukup.gauravpc.noteit.Notes;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.bukup.gauravpc.noteit.MainActivity;
import com.bukup.gauravpc.noteit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class edit_note extends AppCompatActivity {

    String user_id,id_val;
    EditText new_note;
    EditText noteText_title,noteText_data,noteText_date;
    ImageView saveImg;
    String title="",body="";
    String titleBeforeChange="",titleAfterChange="",bodyBeforeAnyChange="",bodyAfterAnyChange="";
    private android.os.Handler handler;
    String title_val,body_val;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        user_id=sharedPreferences.getString("user_id", " ");

        Intent intent=getIntent();

        id_val=intent.getStringExtra("id");
        titleBeforeChange=title_val=intent.getStringExtra("title");
        bodyBeforeAnyChange=body_val=intent.getStringExtra("body");

        noteText_title=(EditText)findViewById(R.id.title);noteText_title.setText(title_val);
        noteText_data=(EditText)findViewById(R.id.body);noteText_data.setText(body_val);

        RelativeLayout saveLayout,audioLayout,imageLayout,backupLayout,infoLayout,deleteNoteLayout;
        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
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

                final Dialog dialog = new Dialog(edit_note.this);
                dialog.setContentView(R.layout.layout_ask_before_delete);
                CardView yes,no;
                yes=(CardView)dialog.findViewById(R.id.yesLayout);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseHandler db = new DatabaseHandler(edit_note.this);
                        db.deleteNote(Integer.parseInt(id_val));
                        db.close();

                        runnable_delete(id_val);

                        Toast.makeText(getApplicationContext(),"Note Deleted",Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent();
                        intent1.putExtra("status", "deleted");
                        setResult(RESULT_OK, intent1);
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

        ImageView imageView=(ImageView)findViewById(R.id.goBackImg);
        imageView.setOnClickListener(new View.OnClickListener() {
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
                                Log.d("status","note deleted");
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
                        params.put("table","notes");
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
    public void updateNote(){
        title=noteText_title.getText().toString();
        body=noteText_data.getText().toString();
        if (title.length() < 1) {
            noteText_title.setText("No Title");
        }

        String noteDate;
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
        Date date = new Date();
        noteDate = df.format(date);

        final DatabaseHandler db = new DatabaseHandler(edit_note.this);
        db.updateNote(id_val, noteDate, makeFirstUpper(title),body);

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

        if(title.length()<1 && body.length()<1){
            titleAfterChange=noteText_title.getText().toString();
            bodyAfterAnyChange=noteText_data.getText().toString();

            if(titleAfterChange.equals(titleBeforeChange) && bodyAfterAnyChange.equals(bodyBeforeAnyChange)){
                super.onBackPressed();
            }else{
                final Dialog d = new Dialog(edit_note.this);
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
                        updateNote();
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
                noteText_data=(EditText)findViewById(R.id.body);
                String text=noteText_data.getText().toString();
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(text.length()<1){
                    text+=""+result.get(0);
                }
                else
                    text+="\n"+result.get(0);
                noteText_data.setText(text);
            }
                break;
        }
    }
}
