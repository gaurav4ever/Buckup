package com.bukup.gauravpc.noteit.dailyDiary;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.R;
import com.bukup.gauravpc.noteit.viewnotes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditPage_Diary extends AppCompatActivity {

    EditText new_note;
    EditText title,body;
    ImageView saveImg;
    TextView dairyPageText_day,dairyPageText_day_ordinal,dairyPageText_month,dairyPageText_year;
    String id_val,date_val,title_val,body_val,day_val,day_ordinal_val,month_val,year_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page__diary);

        Intent intent=getIntent();

        id_val=intent.getStringExtra("id");
        date_val=intent.getStringExtra("date");
        title_val=intent.getStringExtra("title");
        body_val=intent.getStringExtra("body");
        day_val=intent.getStringExtra("day");
        day_ordinal_val=intent.getStringExtra("day_ordinal");
        month_val=intent.getStringExtra("month");
        year_val=intent.getStringExtra("year");

        title=(EditText)findViewById(R.id.title);title.setText(title_val);
        body=(EditText)findViewById(R.id.body);body.setText(body_val);

        dairyPageText_day=(TextView)findViewById(R.id.day);dairyPageText_day.setText(day_val);
        dairyPageText_day_ordinal=(TextView) findViewById(R.id.ordinal);dairyPageText_day_ordinal.setText(day_ordinal_val);
        dairyPageText_month=(TextView) findViewById(R.id.month);dairyPageText_month.setText(month_val);
        dairyPageText_year=(TextView) findViewById(R.id.year);dairyPageText_year.setText(year_val);

        final TextView updatedAtTextView=(TextView)findViewById(R.id.updateAtText);
        updatedAtTextView.setVisibility(View.GONE);

        RelativeLayout saveLayout,audioLayout,imageLayout,backupLayout,infoLayout,deleteNoteLayout;
        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = (EditText) findViewById(R.id.title);
                if (title.getText().toString().length() < 1) {
                    body.setText("No Title");
                }
                body = (EditText) findViewById(R.id.body);

                final DatabaseHandler db = new DatabaseHandler(EditPage_Diary.this);
                db.updatePage_diary(id_val, date_val, makeFirstUpper(title.getText().toString()), body.getText().toString());

                updatedAtTextView.setVisibility(View.VISIBLE);
                updatedAtTextView.setText("Diary page updated");

                db.close();
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
                        Intent intent1 = new Intent(EditPage_Diary.this, ViewDiary.class);
                        startActivity(intent1);
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
    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(EditPage_Diary.this,ViewDiary.class);
        startActivity(intent);
        finish();
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
